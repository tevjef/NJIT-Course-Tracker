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

import com.tevinjeffrey.njitct.model.InstructorModel;

public class NInstructor extends InstructorModel {
    private String firstName;
    private String lastName;

    public NInstructor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
    }

    protected NInstructor(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
    }

    public static final Creator<NInstructor> CREATOR = new Creator<NInstructor>() {
        public NInstructor createFromParcel(Parcel source) {
            return new NInstructor(source);
        }

        public NInstructor[] newArray(int size) {
            return new NInstructor[size];
        }
    };
}
