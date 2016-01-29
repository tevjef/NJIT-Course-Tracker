package main

import (
	"github.com/gin-gonic/gin"
	"strconv"
	"database/sql"
	_ "github.com/lib/pq"
	"fmt"
)

var database *sql.DB

func init() {
	dbInfo := fmt.Sprintf("postgres://%s:%s@%s:5432/%s",
		DbUser, DbPassword, DbHost, DbName)

	db, err := sql.Open("postgres", dbInfo)
	database = db
	checkError(err)
}

func main() {
	gin.SetMode(gin.ReleaseMode)
	r := gin.Default()
	r.GET("/courses", courseHandler)
	r.GET("/sections", sectionHandler)
	r.GET("/subjects", subjectHandler)
	r.Run(":80")
}

func courseHandler(c *gin.Context) {
	s := QueryCourses(formatSemester(c.Query("semester")), c.Query("subject"))
	c.String(200, s)
}

func subjectHandler(c *gin.Context) {
	s := QuerySubjects(formatSemester(c.Query("semester")))
	c.JSON(200, s)
}

func sectionHandler(c *gin.Context) {
	s := QuerySection(formatSemester(c.Query("semester")), c.Query("index"))
	c.String(200, s)
}

func formatSemester(s string) (semester Semester) {
	n, _ := strconv.Atoi(s[1:])
	semester.Year = n
	if s[:1] == "f" {
		semester.Season = FALL
		return
	} else if s[:1] == "s" {
		semester.Season = SPRING
		return
	} else if s[:1] == "u" {
		semester.Season = SUMMER
		return
	} else if s[:1] == "w" {
		semester.Season = WINTER
		return
	}
	return
}

func QueryCourses(semester Semester, subject string) string {
	var data sql.NullString

	database.QueryRow(`SELECT subject.data_json FROM subject WHERE abbr = $1 AND semester = $2`,
	ToNullString(subject),
	ToNullString(semester.String())).Scan(&data)
	return data.String
}

func QuerySection(semester Semester, index string) string {
	var data sql.NullString

	database.QueryRow(`SELECT section.data_json FROM section
	JOIN course ON course.cid = section.cid
	JOIN subject ON subject.subid = course.subid
	WHERE section.c_number = $1 AND subject.semester = $2`,
		ToNullString(index),
		ToNullString(semester.String())).Scan(&data)
	return data.String
}

func QuerySubjects(semester Semester) []Subject {
	rows, err := database.Query(`SELECT abbr, name FROM subject WHERE semester = $1 ORDER BY abbr ASC`,
	ToNullString(semester.String()))
	checkError(err)

	return getSubjectsFromRows(rows)
}

func getSubjectsFromRows(rows *sql.Rows) (subjects []Subject) {
	for rows.Next() {
		var id sql.NullString
		var name sql.NullString

		rows.Scan(&id, &name)

		subjects = append(subjects, Subject{
			SubjectId:id.String,
			SubjectName:name.String,
		})
	}
	return
}

type (
	Subject struct {
		SubjectId   string `json:"abbr,omitempty"`
		SubjectName string `json:"name,omitempty"`
		Semester Semester `json:"-"`
	}

	Course struct {
		CourseName        string    `json:"name,omitempty"`
		CourseNum         string    `json:"number,omitempty"`
		CourseDescription string    `json:"description,omitempty"`
		section           []Section `json:"sections,omitempty"`
	}

	Section struct {
		SectionNum   string        `json:"section_number,omitempty"`
		CallNum      string        `json:"call_number,omitempty"`
		MeetingTimes []MeetingTime `json:"meeting_time,omitempty"`
		Status       string        `json:"status,omitempty"`
		Max          float64        `json:"max,omitempty"`
		Now          float64        `json:"now,omitempty"`
		Instructor   string        `json:"instructor,omitempty"`
		BookUrl      string        `json:"book_url,omitempty"`
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

