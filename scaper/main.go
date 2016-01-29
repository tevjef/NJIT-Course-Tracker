package main

import (
	"bufio"
	"database/sql"
	"encoding/json"
	"fmt"
	"github.com/PuerkitoBio/goquery"
	_ "github.com/lib/pq"
	"io/ioutil"
	"os"
	"regexp"
	"strings"
	"sync"
	"time"
	"strconv"
)

var database *sql.DB

const (
	CURRENT_YEAR     = "2015"
	CURRENT_SEMESTER = "f"
)

func init() {
	dbInfo := fmt.Sprintf("postgres://%s:%s@%s:5432/%s",
		DbUser, DbPassword, DbHost, DbName)
	db, err := sql.Open("postgres", dbInfo)
	checkError(err)
	database = db
}

func main() {

	var wg sync.WaitGroup

	allsemesters := make(map[int][]Subject)
	allsemesters[0] = getSubjectList(resolveSemesters(time.Now()).Next)
	allsemesters[1] = getSubjectList(resolveSemesters(time.Now()).Last)
	allsemesters[2] = getSubjectList(resolveSemesters(time.Now()).Current)

	for _, subjectlist := range allsemesters {
		for _, subject := range subjectlist {
			fmt.Printf("%#v", subject)
			subid := insertSubject(subject, database)
			wg.Add(1)
			go func(sub Subject) {
				courselist := getCourses(sub)
				insertCourseJSON(courselist, subid, database)
				for course_index, course := range courselist {
					fmt.Printf("%#v", course)
					cid := insertCourse(course, subid, course_index, database)
					for section_index, section := range course.Section {
						fmt.Printf("%#v", section)
						sid := insertSection(section, cid, section_index, database)
						for meeting_index, meeting := range section.MeetingTimes {
							fmt.Printf("%#v", meeting)
							insertMeeting(meeting, sid, meeting_index, database)
						}
					}
					b, err := json.Marshal(course)
					if err != nil {
						fmt.Println(err)
						return
					}
					ioutil.WriteFile("courses/" + subject.SubjectId + "-" + subject.SubjectName +"-"+ subject.Semester.String()+
					".json", b, os.ModePerm)
				}
				wg.Done()
			}(subject)
		}
	}
	wg.Wait()
}


func getSubjectList(semester Semester) []Subject {
	fmt.Println("GETTING SUBJECT LIST")
	url := fmt.Sprintf("https://courseschedules.njit.edu/index.aspx?semester=%s%s",
		strconv.Itoa(semester.Year), semester.Season.String())
	fmt.Println("Getting: ",url)

	doc, err := goquery.NewDocument(url)
	checkError(err)

	return extractSubjectList(doc, semester)
}

func extractSubjectList(doc *goquery.Document, semester Semester) (subjectList []Subject) {
	doc.Find(".dashed_wrapper a").Each(func(i int, s *goquery.Selection) {
		subjectList = append(subjectList, Subject{
			SubjectId:   trim(substringBefore(s.Text(), "-")),
			SubjectName: trim(substringAfter(s.Text(), "-")),
			Semester:semester})
	})
	return
}


func getCourses(subject Subject) (courses []Course) {
	var url = fmt.Sprintf("https://courseschedules.njit.edu/index.aspx?semester=%s%s&subjectID=%s",
	strconv.Itoa(subject.Semester.Year), subject.Semester.Season.String(), subject.SubjectId)
	fmt.Println("Url: ", url)
	doc, err := goquery.NewDocument(url)
	if err != nil {
		fmt.Printf("%s", err)
		os.Exit(1)
	}

	return extractCourseList(doc)
}

func extractCourseList(doc *goquery.Document) (courses []Course) {
	doc.Find(".subject_tablewrapper_table tbody").Children().Each(func(i int, s *goquery.Selection) {
		if i > 0 {
			c := Course{
				CourseNum:         extractCourseNum(s),
				CourseName:        extractCourseName(s),
				CourseDescription: extractCourseDescription(s),
				Section:           getSections(s),
			}

			if c.CourseNum != "" {
				courses = append(courses, c)
			}
		}
	})
	return
}

func getSections(s *goquery.Selection) (sections []Section) {
	s.Find(".sectionRow").Each(func(i int, s *goquery.Selection) {
		sections = append(sections, Section{
			SectionNum:   extractSectionNum(s),
			CallNum:      extractCallNum(s),
			MeetingTimes: extractTimes(s),
			Status:       extractStatus(s),
			Max:          extractMaxSize(s),
			Now:          extractNowSize(s),
			Instructor:   extractInstructor(s),
			BookUrl:      extractBookUrl(s),
			Credits:      extractCredits(s),
		})
	})
	return
}

func extractCourseName(selection *goquery.Selection) string {
	return trim(substringBefore(substringAfter(selection.Find(".courseName").Text(), "-"), "("))
}

func extractCourseNum(selection *goquery.Selection) string {
	return trim(substringAfterLast(trim(substringBefore(selection.Find(".courseName").Text(), "-")), " "))
}

func extractCourseDescription(selection *goquery.Selection) string {
	return trim(selection.Find(".tooltip").Text())
}

func extractSectionNum(selection *goquery.Selection) string {
	return trim(selection.Find(".section").Text())
}

func extractCallNum(selection *goquery.Selection) string {
	return trim(selection.Find(".call span").Text())
}

func extractBookUrl(selection *goquery.Selection) string {
	return trim(selection.Find(".call a").AttrOr("href", ""))
}

func extractRoomNum(selection *goquery.Selection) string {
	s, _ := selection.Find(".room").Html()
	s = strings.Replace(s, "<br/>", "\n", -1)
	doc, err := goquery.NewDocumentFromReader(strings.NewReader(s))
	if err != nil {
		fmt.Print(err)
	}
	return trim(doc.Text())
}

func extractTimes(selection *goquery.Selection) (meetingTimes []MeetingTime) {
	s, _ := selection.Find(".call").Next().Html()
	s = strings.Replace(s, "<br/>", "\n", -1)
	s = strings.Replace(s, "\t", "", -1)
	regex, _ := regexp.Compile(`\s\s+`)
	s = regex.ReplaceAllString(s, "")
	s = substringBefore(s, "Sec")
	rawroom := extractRoomNum(selection)
	rooms := strings.Split(rawroom, "\n")
	doc, err := goquery.NewDocumentFromReader(strings.NewReader(s))
	if err != nil {
		fmt.Print(err)
	}
	result := trim(doc.Text())
	scanner := bufio.NewScanner(strings.NewReader(result))
	j := 0
	for scanner.Scan() {
		var room string
		if len(rooms) > j {
			room = rooms[j]
		}
		j++
		text := scanner.Text()
		i := 0
		for {
			day := text[i]
			if day == 'M' || day == 'T' || day == 'W' || day == 'R' || day == 'F' || day == 'S' {
				start, end := getStartAndEnd(text)
				meetingTimes = append(meetingTimes, MeetingTime{
					Day:       string(day),
					StartTime: start,
					EndTime:   end,
					Room:      room,
				})
				getStartAndEnd(text)
			} else {
				break
			}
			i++
		}

	}
	return
}

func getStartAndEnd(time string) (string, string) {
	r, _ := regexp.Compile("\\d{3,4}(AM|PM)")
	result := r.FindAllString(time, -1)
	if result != nil {
		return formatTime(result[0]), formatTime(result[1])
	}
	return "", ""
}

func formatTime(time string) string {
	if len(time) == 6 {
		return time[:2] + ":" + time[2:]
	}
	if len(time) == 5 {
		return time[:1] + ":" + time[1:]
	}
	return time
}

func extractStatus(selection *goquery.Selection) string {
	return trim(selection.Find(".status").Text())
}

func extractMaxSize(selection *goquery.Selection) float64 {
	return ToFloat64(selection.Find(".max").Text())
}

func extractNowSize(selection *goquery.Selection) float64 {
	return ToFloat64(trim(selection.Find(".now").Text()))
}

func extractInstructor(selection *goquery.Selection) string {
	return trim(selection.Find(".instructor").Text())
}

func extractCredits(selection *goquery.Selection) string {
	if result :=trim (selection.Find(".credits").Text()); strings.Contains(result, "#") {
		return "0"
	} else {
		return result
	}
}

type (
	Subject struct {
		SubjectId   string `json:"sid,omitempty"`
		SubjectName string `json:"name,omitempty"`
		Semester Semester
	}

	Course struct {
		CourseName        string    `json:"name,omitempty"`
		CourseNum         string    `json:"number,omitempty"`
		CourseDescription string    `json:"description,omitempty"`
		Section           []Section `json:"sections,omitempty"`
	}

	Section struct {
		SectionNum   string        `json:"section_number,omitempty"`
		CallNum      string        `json:"call_number,omitempty"`
		Status       string        `json:"status,omitempty"`
		Max          float64        `json:"max,omitempty"`
		Now          float64        `json:"now,omitempty"`
		Instructor   string        `json:"instructor,omitempty"`
		BookUrl      string        `json:"book_url,omitempty"`
		MeetingTimes []MeetingTime `json:"meeting_time,omitempty"`
		Credits      string        `json:"credits,omitempty"`
	}

	MeetingTime struct {
		Day       string `json:"day,omitempty"`
		StartTime string `json:"start_time,omitempty"`
		EndTime   string `json:"end_time,omitempty"`
		Room      string `json:"room,omitempty"`
	}

	Season int

	Semester struct {
		Year   int
		Season Season
	}

	ResolvedSemester struct {
		Last    Semester
		Current Semester
		Next    Semester
	}
)

const (
	FALL Season = iota
	SPRING
	SUMMER
	WINTER
)

var seasons = [...]string{
	"f",
	"s",
	"u",
	"w",
}

func resolveSemesters(t time.Time) ResolvedSemester {
	month := t.Month()
	day := t.Day()
	year := t.Year()

	fall := Semester{
		Year:   year,
		Season: FALL}

	winter := Semester{
		Year:   year,
		Season: WINTER}

	spring := Semester{
		Year:   year,
		Season: SPRING}

	summer := Semester{
		Year:   year,
		Season: SUMMER}

	//Oct 1 <-> Jan 15
	if (month >= time.October && day >= 1) || (month == time.January && day < 15) {
		spring.Year = spring.Year + 1
		return ResolvedSemester{Last: fall, Current: winter, Next: spring}
	}
	//Jan 15 <-> March 15
	if (month == time.January && day >= 15) || (month <= time.March && day < 15) {
		return ResolvedSemester{
			Last:    winter,
			Current: spring,
			Next:    summer,
		}
	}
	//March 15 <-> September 1
	if (month >= time.March && day >= 15) || (month <= time.September && day <= 31) {
		return ResolvedSemester{
			Last:    spring,
			Current: summer,
			Next:    fall,
		}
	}
	return ResolvedSemester{
		Last:    spring,
		Current: summer,
		Next:    fall,
	}
}

func (s Semester) String() string {
	if (s.Season == FALL) {
		return "Sep-01-"+ strconv.Itoa(s.Year)
	} else if (s.Season == WINTER) {
		return "Dec-01-"+ strconv.Itoa(s.Year)
	} else if (s.Season == SPRING) {
		return "Feb-01-"+ strconv.Itoa(s.Year)
	} else {
		return "Jun-01-"+ strconv.Itoa(s.Year)
	}
}

func (s Season) String() string {
	return seasons[s]
}
