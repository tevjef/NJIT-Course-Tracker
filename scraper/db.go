package main
import (
	"database/sql"
	"fmt"
	"encoding/json"
)

func insertSubject(subject Subject, db *sql.DB) string {
	var subid sql.NullString

	row := database.QueryRow(`UPDATE subject SET abbr = $1, name = $2 WHERE abbr = $3 AND semester = $4 RETURNING subid`,
		ToNullString(subject.SubjectId),
		ToNullString(subject.SubjectName),
		ToNullString(subject.SubjectId),
		ToNullString(subject.Semester.String()))
	row.Scan(&subid)

	fmt.Printf("Row affected on update subject %s\n",subid.String)

	if (subid.String == "") {
		row := database.QueryRow(`INSERT INTO subject (abbr, name, semester) VALUES($1, $2, $3) RETURNING subid`,
		ToNullString(subject.SubjectId),
		ToNullString(subject.SubjectName),
		ToNullString(subject.Semester.String()))
		err := row.Scan(&subid)
		checkError(err)

		fmt.Println("Inserting: ", subject.SubjectId)
		fmt.Printf("Rows affected on insert subject %s\n",subid.String)
	}
	return subid.String
}

func insertCourseJSON(courselist []Course, subid string, db *sql.DB) {
	var id sql.NullString

	b, err := json.Marshal(courselist)
	if err != nil {
		fmt.Println(err)
	}
	json := string(b[:])
	if len(json) == 0 {
		panic(fmt.Sprintf("Course json empty::subid %s", subid))
	}

	row := database.QueryRow(`UPDATE subject SET data_json = $1 WHERE subid = $2 RETURNING subid`,
	ToNullString(json),
	Atoi64(subid))
	err = row.Scan(&id)
	checkError(err)
	fmt.Printf("Rows affected on update course json %s\n",id.String)
}

func insertCourse(course Course, subid string, index int, db *sql.DB) string {
	var cid sql.NullString

	b, err := json.Marshal(course)
	if err != nil {
		fmt.Println(err)
	}
	json := string(b[:])

	row := database.QueryRow(`UPDATE course SET subid = $1, name = $2, number = $3, description = $4, data_json = $5 FROM subject WHERE
	subject.subid = course.subid AND
	subject.subid = $6 AND course.index = $7 RETURNING cid`,
		Atoi64(subid),
		ToNullString(course.CourseName),
		ToNullString(course.CourseNum),
		ToNullString(course.CourseDescription),
		ToNullString(json),
		Atoi64(subid),
		ToNullInt64(int64(index)))
	row.Scan(&cid)
	fmt.Printf("Rows affected on update course %s\n",cid.String)
	if cid.String == "" {
		row := db.QueryRow(`INSERT INTO course (subid, name, number, description, index, data_json) VALUES($1,$2,$3,$4,$5,$6) RETURNING cid`,
			Atoi64(subid),
			ToNullString(course.CourseName),
			ToNullString(course.CourseNum),
			ToNullString(course.CourseDescription),
			ToNullInt64(int64(index)),
			ToNullString(json))
		err := row.Scan(&cid)
		checkError(err)
		fmt.Printf("Rows affected on insert course %s\n",cid.String)
	}
	return cid.String
}

func insertSection(section Section, cid string, index int, db *sql.DB) string {
	var sid sql.NullString

	b, err := json.Marshal(section)
	if err != nil {
		fmt.Println(err)
	}
	json := string(b[:])

	row := db.QueryRow(`UPDATE section SET cid = $1, s_number = $2, c_number = $3, status = $4,
	 max = $5, now = $6, instructor = $7, book_url = $8, credits = $9, data_json = $10 FROM course WHERE course.cid = section.cid AND
	 course.cid = $11 AND section.index = $12 RETURNING sid`,
		Atoi64(cid),
		ToNullString(section.SectionNum),
		ToNullString(section.CallNum),
		ToNullString(section.Status),
		ToNullFloat64(section.Max),
		ToNullFloat64(section.Now),
		ToNullString(section.Instructor),
		ToNullString(section.BookUrl),
		ToNullString(section.Credits),
		ToNullString(json),
		Atoi64(cid),
		ToNullInt64(int64(index)))
	row.Scan(&sid)
	fmt.Printf("Rows affected on update section %s\n",sid.String)
	if sid.String == "" {
		row := db.QueryRow(`INSERT INTO section(cid, s_number, c_number, status, max, now, instructor, book_url, credits, index, data_json)
		VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11) RETURNING sid`,
			Atoi64(cid),
			ToNullString(section.SectionNum),
			ToNullString(section.CallNum),
			ToNullString(section.Status),
			ToNullFloat64(section.Max),
			ToNullFloat64(section.Now),
			ToNullString(section.Instructor),
			ToNullString(section.BookUrl),
			ToNullString(section.Credits),
			ToNullInt64(int64(index)),
			ToNullString(json))
		err := row.Scan(&sid)
		checkError(err)

		fmt.Printf("Rows affected on insert section %s\n",sid.String)
	}
	return sid.String
}

func insertMeeting(meeting MeetingTime, sid string, index int, db *sql.DB) string {
	var mid sql.NullString

	row := db.QueryRow(`UPDATE meeting SET sid = $1, start_time = $2, end_time = $3, day = $4, room = $5 FROM section
	WHERE section.sid = meeting.sid AND section.sid = $6 AND meeting.index = $7 RETURNING mid`,
		Atoi64(sid),
		ToNullString(meeting.StartTime),
		ToNullString(meeting.EndTime),
		ToNullString(meeting.Day),
		ToNullString(meeting.Room),
		Atoi64(sid),
		ToNullInt64(int64(index)))
	row.Scan(&mid)
	fmt.Printf("Rows affected on update meeting  %s\n",mid.String)

	if mid.String == "" {
		row := db.QueryRow(`INSERT INTO meeting (sid, start_time, end_time, day, room, index) VALUES($1,$2,$3,$4,$5,$6) RETURNING mid`,
			Atoi64(sid),
			ToNullString(meeting.StartTime),
			ToNullString(meeting.EndTime),
			ToNullString(meeting.Day),
			ToNullString(meeting.Room),
			ToNullInt64(int64(index)))
		err := row.Scan(&mid)
		checkError(err)
		fmt.Printf("Rows affected on insert meeting %s\n",mid.String)
	}
	return mid.String
}