package main

import (
	"encoding/json"
	"fmt"
	"github.com/PuerkitoBio/goquery"
	"github.com/stretchr/testify/assert"
	"io/ioutil"
	"log"
	"os"
	"testing"
	"time"
)

var SubjectDoc *goquery.Document
var CourseDoc *goquery.Document

const (
	COURSES  = "testRes/NJIT_Course.test"
	SUBJECTS = "testRes/NJIT_Subjects.test"
)

var season = resolveSemesters(time.Now()).Current

func setup() {
	var err error
	if SubjectDoc == nil && CourseDoc == nil {
		CourseDoc = getDummyDoc(COURSES)
		SubjectDoc = getDummyDoc(SUBJECTS)
		if err != nil {
			log.Fatal("Error when getting course or subject")
		}
	}
}

func TestExtractFullCourses(t *testing.T) {
	doc, err := goquery.NewDocument(fmt.Sprintf("https://courseschedules.njit.edu/index.aspx?semester=%s%s",
		CURRENT_YEAR, CURRENT_SEMESTER))
	if err != nil {
		fmt.Printf("%s", err)
		os.Exit(1)
	}
	sublist := extractSubjectList(doc, season)
	fmt.Printf("\nSUBLIST %v\n", sublist)
	limiter := time.Tick(time.Millisecond * 10)

	done := make(chan bool)
	for _, subject := range sublist {
		<-limiter
		go func(subject Subject) {
			result := getCourses(subject)
			b, err := json.Marshal(result)
			if err != nil {
				fmt.Println(err)
				return
			}
			ioutil.WriteFile("courses/"+subject.SubjectId+" - "+subject.SubjectName+".json", b, os.ModePerm)
			done <- true
		}(subject)
	}
	<-done
}

func TestExtractSubjectList(t *testing.T) {
	setup()
	result := extractSubjectList(SubjectDoc, season)
	assert.True(t, len(result) > 0)
	b, err := json.Marshal(result)
	if err != nil {
		fmt.Println(err)
		return
	}
	ioutil.WriteFile("subjects.json", b, os.ModePerm)
}

func TestExtractCourseName(t *testing.T) {
	setup()
	result := extractCourseName(CourseDoc.Find(".subject_wrapper").First())
	fmt.Print(result)
	assert.True(t, len(result) > 0)
}

func TestExtractCourseNum(t *testing.T) {
	setup()
	expected := "100"
	result := extractCourseNum(CourseDoc.Find(".subject_wrapper").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestTime(t *testing.T) {
	times := time.Now()
	assert.True(t, times.Month() < times.Month()+1)
}

func TestGetResolvedSemester(t *testing.T) {
	rs := resolveSemesters(time.Now())
	log.Printf("%v", rs)
	rs = resolveSemesters(time.Date(2009, time.July, 10, 23, 0, 0, 0, time.UTC))
	log.Printf("%v", rs)
	rs = resolveSemesters(time.Date(2009, time.January, 15, 23, 0, 0, 0, time.UTC))
	log.Printf("%v", rs)
	rs = resolveSemesters(time.Date(2009, time.January, 14, 23, 0, 0, 0, time.UTC))
	log.Printf("%v", rs)

}

func TestExtractCourseDescription(t *testing.T) {
	setup()
	result := extractCourseDescription(CourseDoc.Find(".subject_wrapper").First())
	fmt.Print(result)
	assert.True(t, len(result) > 0)
}

func TestGetCourses(t *testing.T) {
	setup()
	expected := 74
	result := getCourses(Subject{SubjectId:"CS", SubjectName:"Computer Science"})
	fmt.Printf("%#v", result)
	assert.Equal(t, expected, len(result))

	b, err := json.Marshal(result)
	if err != nil {
		fmt.Println(err)
		return
	}
	ioutil.WriteFile("courses.json", b, os.ModePerm)
}

func TestGetSections(t *testing.T) {
	setup()
	expected := 2
	result := getSections(CourseDoc.Find(".subject_tablewrapper_table tbody tr").Siblings().Eq(1))
	fmt.Printf("%#v", result)
	assert.Equal(t, expected, len(result))
}

func TestExtractSectionNum(t *testing.T) {
	setup()
	expected := "001"
	result := extractSectionNum(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractCallNum(t *testing.T) {
	setup()
	expected := "91357"
	result := extractCallNum(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractBookUrl(t *testing.T) {
	setup()
	expected := "http://www.bkstr.com/webapp/wcs/stores/servlet/booklookServlet?bookstore_id-1=584&term_id-1=fall 2015&crn-1=91357"
	result := extractBookUrl(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractRoomNum(t *testing.T) {
	setup()
	expected := "GITC2400\nGITC1100"
	result := extractRoomNum(CourseDoc.Find(".subject_tablewrapper_table tbody tr").Siblings().Eq(3))
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractMeetingTime(t *testing.T) {
	setup()
	result := extractTimes(CourseDoc.Find(".subject_tablewrapper_table tbody tr").Siblings().Eq(3))
	log.Println(result)
	//assert.Equal(t, expected, result)
}

func TestGetStartAndEnd(t *testing.T) {
	time := "M:1000AM - 125PM\nW:100PM - 225PM"
	result1, result2 := getStartAndEnd(time)
	assert.Equal(t, "1000AM", result1)
	assert.Equal(t, "125PM", result2)
}

func TestFormatTime(t *testing.T) {
	result := formatTime("1101AM")
	assert.Equal(t, "11:01AM", result)
	result = formatTime("101AM")
	assert.Equal(t, "1:01AM", result)
}

func TestExtractStatus(t *testing.T) {
	setup()
	expected := "Closed"
	result := extractStatus(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractMaxSize(t *testing.T) {
	setup()
	expected := "27"
	result := extractMaxSize(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractNowSize(t *testing.T) {
	setup()
	expected := "27"
	result := extractNowSize(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractInstructor(t *testing.T) {
	setup()
	expected := "Vaks Leon"
	result := extractInstructor(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestExtractCredits(t *testing.T) {
	setup()
	expected := "3.00"
	result := extractCredits(CourseDoc.Find(".subject_wrapper .sectionRow").First())
	fmt.Print(result)
	assert.Equal(t, expected, result)
}

func TestSemesterString(t *testing.T) {
	s := Semester{
		Year:   2015,
		Season: 0,
	}
	log.Println(s.String())
}
