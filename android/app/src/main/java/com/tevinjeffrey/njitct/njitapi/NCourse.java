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

import com.tevinjeffrey.njitct.model.CourseModel;
import com.tevinjeffrey.njitct.model.Metadata;
import com.tevinjeffrey.njitct.model.SectionModel;

import java.util.ArrayList;
import java.util.List;

public class NCourse implements CourseModel {
    String name;
    String number;
    String description;
    NSubject subject;
    List<NSection> sections;
    List<? extends Metadata> courseMetadata;

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }


    @Override
    public NSubject getSubject() {
        return subject;
    }

    public void setSubject(NSubject subject) {
        this.subject = subject;
    }

    @Override
    public List<? extends SectionModel> getSections() {
        return sections;
    }

    @Override
    public List<? extends Metadata> getCourseMetadata() {
        ArrayList<Metadata> arrayList = new ArrayList<>();
        if (description != null) {
            arrayList.add(new Metadata("Synopsis", description));
        }
        return arrayList;
    }

    @Override
    public String getTotalSections() {
        return String.valueOf(sections.size());
    }

    @Override
    public String getOpenSections() {
        int size = 0;
        for (NSection s : sections) {
            if (s.isOpen()) {
                size++;
            }
        }
        return String.valueOf(size);
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getTerm() {
        return getSubject().getSemester().toString();
    }


    public NCourse() {
    }

    public NCourse(NCourse course) {
        this.courseMetadata = course.getCourseMetadata();
        this.name = course.getName();
        this.number = course.getNumber();
        this.subject = course.getSubject();
        this.description = course.getDescription();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeParcelable(this.subject, 0);
        dest.writeTypedList(sections);
        dest.writeTypedList(courseMetadata);
    }

    protected NCourse(Parcel in) {
        this.name = in.readString();
        this.number = in.readString();
        this.subject = in.readParcelable(NSubject.class.getClassLoader());
        this.sections = in.createTypedArrayList(NSection.CREATOR);
        this.courseMetadata = in.createTypedArrayList(Metadata.CREATOR);
    }

    public static final Creator<NCourse> CREATOR = new Creator<NCourse>() {
        public NCourse createFromParcel(Parcel source) {
            return new NCourse(source);
        }

        public NCourse[] newArray(int size) {
            return new NCourse[size];
        }
    };
}
