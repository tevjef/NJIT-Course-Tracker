# njit-scraper
An application to scrape data from NJIT's course schedule at http://courseschedules.njit.edu/

### Prerequisites

The scraper is written in Go, so making sure you have latest version of the compiler on you system. 

`https://golang.org/doc/install`

Once you have Go on you machine and the `GOTPATH` environment variable setup. Copy this project to /src/njit-scraper. 

Go's dependency management is currently pretty primitive so download these open source libraries first. 

```

go get github.com/PuerkitoBio/goquery

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

`go install src/njit-scraper`



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
