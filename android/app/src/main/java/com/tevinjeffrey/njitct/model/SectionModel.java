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

package com.tevinjeffrey.njitct.model;

import android.os.Parcelable;

import java.util.List;

public interface SectionModel extends Parcelable, Comparable {
    String getSectionNumber();
    // Some unique string to indentify a particular section.
    String getIndexNumber();
    boolean isOpen();
    String getMax();
    String getNow();
    String getCredits();
    CourseModel getCourse();
    List<? extends InstructorModel> getInstructor();
    List<? extends MeetingTimeModel> getMeetingTime();
    List<? extends Metadata> getMetaData();
    int hashCode();
}