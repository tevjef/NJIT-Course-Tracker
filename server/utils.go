package main

import (
	"log"
	"database/sql"
	"strconv"
)

const (
	Empty         = ""
	IndexNotFound = -1
)

func checkError(err error) {
	if err != nil {
		log.Panic(err)
	}
}


//ToNullString invalidates a sql.NullString if empty, validates if not empty
func ToNullString(s string) sql.NullString {
	return sql.NullString{String : s, Valid : s != ""}
}

func ToNullFloat64(f float64) sql.NullFloat64 {
	return sql.NullFloat64{Float64 : f, Valid : f != 0}
}

func ToNullBool(b bool) sql.NullBool {
	return sql.NullBool{Bool : b, Valid : true}
}

func ToNullInt64(i int64) sql.NullInt64 {
	return sql.NullInt64{Int64 : i, Valid : true}
}

func Atoi64(str string) sql.NullInt64 {
	if val, err := strconv.Atoi(str); err == nil {
		return ToNullInt64(int64(val))
	}
	return sql.NullInt64{Int64 : 0, Valid : true}
}

func ToFloat64(str string) float64 {
	s, err := strconv.ParseFloat(str, 64)
	checkError(err)
	return s
}

