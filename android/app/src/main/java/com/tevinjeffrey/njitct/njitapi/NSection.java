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
import android.os.Parcelable;

import com.tevinjeffrey.njitct.model.CourseModel;
import com.tevinjeffrey.njitct.model.InstructorModel;
import com.tevinjeffrey.njitct.model.MeetingTimeModel;
import com.tevinjeffrey.njitct.model.Metadata;
import com.tevinjeffrey.njitct.model.SectionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NSection implements SectionModel, Parcelable {
    String sectionNumber;
    String callNumber;
    String max;
    String now;
    String status;
    String instructor;
    String bookUrl;
    String credits;
    NCourse course;
    List<NMeetingTime> meetingTime;

    public String getSectionNumber() {
        return sectionNumber;
    }

    public String getIndexNumber() {
        return callNumber;
    }

    @Override
    public boolean isOpen() {
        if (status != null) return status.equals("Open");
        return false;
    }

    public String getMax() {
        return max;
    }

    public String getNow() {
        return now;
    }

    @Override
    public String getCredits() {
        if (credits != null) {
            return String.valueOf(((int) Double.parseDouble(credits)));
        }
        return "0";
    }

    public void setCourse(CourseModel course) {
        this.course = (NCourse) course;
    }

    @Override
    public List<? extends InstructorModel> getInstructor() {
        ArrayList<NInstructor> arrayList = new ArrayList<>();
        if (instructor != null) {
            arrayList.add(new NInstructor(instructor.substring(0, instructor.indexOf(" ")),
                    instructor.substring(instructor.indexOf(" "))));
        }
        return arrayList;
    }

    public NCourse getCourse() {
        return course;
    }

    @Override
    public List<? extends MeetingTimeModel> getMeetingTime() {
        if (meetingTime == null) return new ArrayList<>();
        Collections.sort(meetingTime);
        return meetingTime;
    }

    @Override
    public List<? extends Metadata> getMetaData() {
        ArrayList<Metadata> arrayList = new ArrayList<>();
        if (bookUrl != null) {
            arrayList.add(new Metadata("Book Url", bookUrl));
        }
        return arrayList;
    }

    public NSection() {
    }

    public int hashCode() {
        return getSectionNumber().hashCode() + getIndexNumber().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sectionNumber);
        dest.writeString(this.callNumber);
        dest.writeString(this.max);
        dest.writeString(this.now);
        dest.writeString(this.status);
        dest.writeString(this.instructor);
        dest.writeString(this.bookUrl);
        dest.writeString(this.credits);
        dest.writeParcelable(this.course, 0);
        dest.writeTypedList(meetingTime);
    }

    protected NSection(Parcel in) {
        this.sectionNumber = in.readString();
        this.callNumber = in.readString();
        this.max = in.readString();
        this.now = in.readString();
        this.status = in.readString();
        this.instructor = in.readString();
        this.bookUrl = in.readString();
        this.credits = in.readString();
        this.course = in.readParcelable(NCourse.class.getClassLoader());
        this.meetingTime = in.createTypedArrayList(NMeetingTime.CREATOR);
    }

    public static final Creator<NSection> CREATOR = new Creator<NSection>() {
        public NSection createFromParcel(Parcel source) {
            return new NSection(source);
        }

        public NSection[] newArray(int size) {
            return new NSection[size];
        }
    };

    @Override
    public int compareTo(Object another) {
        int result;
        NSection b = (NSection) another;
        result = new SubjectNumberComparator().compare(this.getCourse().getSubject().getNumber(), b.getCourse().getSubject().getNumber());
        if (result == 0)
            return new SubjectNumberComparator().compare(this.getCourse().getNumber(), b.getCourse().getNumber());
        return result;
    }

    private class SubjectNumberComparator implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    }


}
