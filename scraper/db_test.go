package main
import (
	"database/sql"
	"testing"
	"fmt"
	"time"
	"log"
	"sync"
)

var testDatabase *sql.DB

func dsetup() {
	dbInfo := fmt.Sprintf("postgres://%s:%s@%s:5432/%s",
		DbUser, DbPassword, DbHost, DbName)

	db, err := sql.Open("postgres", dbInfo)
	testDatabase = db
	checkError(err)
}

func tearDown() {
	testDatabase.Close()
}

func TestInsertSubject(t *testing.T) {
	dsetup()
	sublist := getSubjectList(resolveSemesters(time.Now()).Next)
	log.Printf("%v", sublist)
	for _, val := range sublist {
		insertSubject(val, testDatabase)
	}
	tearDown()
}

func TestInsertCourse(t *testing.T) {
	dsetup()
	for _, subject := range getSubjectList(resolveSemesters(time.Now()).Next) {
		log.Printf("%v", subject)
		subid := insertSubject(subject, testDatabase)
		for course_index, course := range getCourses(subject) {
			log.Printf("%v", course)
			insertCourse(course, subid, course_index, testDatabase)
		}
	}
	tearDown()
}

func TestInsertSection(t *testing.T) {
	dsetup()

	var wg sync.WaitGroup
	for _, subject := range getSubjectList(resolveSemesters(time.Now()).Next) {
		log.Printf("%v", subject)
		subid := insertSubject(subject, testDatabase)
		wg.Add(1)
		go func(sub Subject) {
			courselist := getCourses(sub)
			for course_index, course := range courselist {
				log.Printf("%v", course)
				cid := insertCourse(course, subid,course_index,testDatabase)
				for section_index,section := range course.Section {
					log.Printf("%v", section)
					insertSection(section, cid, section_index, testDatabase)
				}
			}
			wg.Done()
		}(subject)
	}
	wg.Wait()
	tearDown()
}

func TestInsertMeetingTimes(t *testing.T) {
	dsetup()

	var wg sync.WaitGroup
	for _, subject := range getSubjectList(resolveSemesters(time.Now()).Next) {
		log.Printf("%v", subject)
		subid := insertSubject(subject, testDatabase)
		wg.Add(1)
		go func(sub Subject) {
			courselist := getCourses(sub)
			insertCourseJSON(courselist, subid, testDatabase)
			for course_index, course := range courselist {
				log.Printf("%v", course)
				cid := insertCourse(course, subid,course_index,testDatabase)
				for section_index,section := range course.Section {
					log.Printf("%v", section)
					sid := insertSection(section, cid, section_index, testDatabase)
					for meeting_index, meeting := range section.MeetingTimes {
						log.Printf("%v", meeting)
						insertMeeting(meeting, sid, meeting_index, testDatabase)
					}
				}
			}
			wg.Done()
		}(subject)
	}
	wg.Wait()
	tearDown()
}