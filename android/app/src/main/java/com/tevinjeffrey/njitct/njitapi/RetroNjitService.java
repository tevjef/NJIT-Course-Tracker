/*
 * Copyright 2015 Tevin Jeffrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tevinjeffrey.njitct.njitapi;


import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface RetroNjitService {
    @GET("/subjects")
    Observable<List<NSubject>> getSubjects(@Query("semester") String semester);

    @GET("/courses")
    Observable<List<NCourse>> getCourses(@Query("semester") String semester, @Query("subject") String subject);

    @GET("/sections")
    Observable<NSection> getSection(@Query("semester") String semester, @Query("index") String index);
}

