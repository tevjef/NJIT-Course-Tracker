# njit-server
A server to create a endpoint for getting NJIT course data.

### Prerequisites

The scraper is written in Go, so making sure you have latest version of the compiler on you system. 

`https://golang.org/doc/install`

Once you have Go on you machine and the `GOTPATH` environment variable setup. Copy this project to /src/njit-scraper. 

Go's dependency management is currently pretty primitive so download these open source libraries first. 

```

go get github.com/gin-gonic/gin

go get github.com/lib/pq

```

The program expects a few constants to serve as login credential for a PosgresSQL database. So create a file `secrets.go` with 

```Go
package main

const (
	DbUser     = ""
	DbHost     = ""
	DbPassword = ""
	DbName     = ""
)

``` 
### Install

`go install src/njit-server`

### Run
`bin/njit-server`

# Usage



#### HTTP Request

`GET http://localhost/courses`

* Gets a list of courses based on a required `semester` and `subject` parameter.

```json

[
  {
    "name": "ROADMAP TO COMPUTING",
    "number": "100",
    "description": "An introduction to programming and problem solving skills using Python or other very high level language. Topics include basic strategies for problem solving, constructs that control the flow of execution of a program and the use of high level data types such as lists, strings and dictionaries in problem representation. The course also presents an overview of selected topics in computing, such as networking and databases.",
    "sections": [
        {
        "section_number": "010",
        "call_number": "11363",
        "meeting_time": [
          {
            "day": "M",
            "start_time": "10:00AM",
            "end_time": "11:25AM",
            "room": "GITC2315B"
          }
        ],
        "status": "Closed",
        "max": 27,
        "now": 27,
        "instructor": "Spirollari Juni",
        "book_url": "http://www.bkstr.com/webapp/wcs/stores/servlet/booklookServlet?bookstore_id-1=584\u0026term_id-1=spring%202016\u0026crn-1=11363",
        "credits": "3.00"
      }
    ]
  }]
```

`GET http://localhost/subject`
* Gets a list of subjects based on a required `semester` and `subject` parameter.

```json

[
  {
    "abbr": "ACC",
    "name": "Accounting/Essex CC"
  },
  {
    "abbr": "ACCT",
    "name": "Accounting"
  }
...
]

```

`GET http://localhost/section`

Returns information about a particular section based on a `semester` and `index`.

```json

{
  "section_number": "002",
  "call_number": "11413",
  "meeting_time": [
    {
      "day": "M",
      "start_time": "10:00AM",
      "end_time": "11:25AM",
      "room": "GITC1400"
    },
    {
      "day": "W",
      "start_time": "10:00AM",
      "end_time": "11:25AM",
      "room": "GITC1400"
    }
  ],
  "status": "Open",
  "max": 80,
  "now": 79,
  "instructor": "Sohn Andrew",
  "book_url": "...",
  "credits": "3.00"
}

```

#### Query Parameters 

**semester**

```

	f = fall
	s = spring
	w = winter
	u = summer 
	
	http://localhost/courses?semester=f2016&subject=CS

```
**subject**

```	
	Follows a 2-4 letter abbreviation scheme for subjects.
	
	http://localhost/courses?semester=f2016&subject=CS

```

**index**

```
	
	A 5 digit number that identifies a particular seciton.
	
	http://localhost/section?semester=f2016&index=10015

```

#License 

```
The MIT License (MIT)

Copyright (c) 2015 NjitCT

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
