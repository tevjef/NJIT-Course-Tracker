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

import com.tevinjeffrey.njitct.model.MeetingTimeModel;

import java.util.Comparator;

public class NMeetingTime implements MeetingTimeModel, Parcelable {
    String room;
    String day;
    String startTime;
    String endTime;
    String meetingType;

    @Override
    public boolean isLecture() {
        return true;
    }

    @Override
    public boolean isRecitation() {
        return false;
    }

    @Override
    public boolean isLab() {
        return false;
    }

    public String getRoom() {
        return room;
    }

    @Override
    public String getFullTime() {
        return startTime + " - " + endTime;
    }

    @Override
    public String getDay() {
        switch (day) {
            case "M":
                return "Monday";
            case "T":
                return "Tuesday";
            case "W":
                return "Wednesday";
            case "R":
                return "Thursday";
            case "F":
                return "Friday";
            case "S":
                return "Saturday";
            default:
                return "Sunday";
        }
    }

    @Override
    public String getStartTime() {
        return startTime;
    }

    @Override
    public String getEndTime() {
        return endTime;
    }

    @Override
    public String getMeetingType() {
        return "Lecture";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.room);
        dest.writeString(this.day);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.meetingType);
    }

    public NMeetingTime() {
    }

    protected NMeetingTime(Parcel in) {
        this.room = in.readString();
        this.day = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.meetingType = in.readString();
    }

    public static final Parcelable.Creator<NMeetingTime> CREATOR = new Parcelable.Creator<NMeetingTime>() {
        public NMeetingTime createFromParcel(Parcel source) {
            return new NMeetingTime(source);
        }

        public NMeetingTime[] newArray(int size) {
            return new NMeetingTime[size];
        }
    };

    @Override
    public int compareTo(Object another) {
        return new TimeRankComparator().compare(this, (NMeetingTime) another);
    }

    private class TimeRankComparator implements Comparator<NMeetingTime> {
        @Override
        public int compare(NMeetingTime lhs, NMeetingTime rhs) {
            if (lhs.getTimeRank() < rhs.getTimeRank()) {
                return 1;
            } else if (lhs.getTimeRank() > rhs.getTimeRank()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public int getTimeRank() {
        if (day != null) {
            switch (day) {
                case "M":
                    return 10;
                case "T":
                    return 9;
                case "W":
                    return 8;
                case "TH":
                    return 7;
                case "F":
                    return 6;
                case "S":
                    return 5;
                case "U":
                    return 4;
            }
        }
        return -1;
    }
}
