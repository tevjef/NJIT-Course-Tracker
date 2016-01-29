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

import android.os.Parcel;

import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.utils.SemesterUtils;

public class NSubject implements SubjectModel {

    String abbr;
    String name;
    SemesterUtils.Semester semester;

    public String getNumber() {
        return abbr;
    }

    public String getName() {
        return name;
    }

    public SemesterUtils.Semester getSemester() {
        return semester;
    }

    @Override
    public void setSemester(SemesterUtils.Semester semester) {
        this.semester = semester;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.abbr);
        dest.writeString(this.name);
        dest.writeParcelable(this.semester, 0);
    }

    public NSubject() {
    }

    protected NSubject(Parcel in) {
        this.abbr = in.readString();
        this.name = in.readString();
        this.semester = in.readParcelable(SemesterUtils.Semester.class.getClassLoader());
    }

    public static final Creator<NSubject> CREATOR = new Creator<NSubject>() {
        public NSubject createFromParcel(Parcel source) {
            return new NSubject(source);
        }

        public NSubject[] newArray(int size) {
            return new NSubject[size];
        }
    };
}
