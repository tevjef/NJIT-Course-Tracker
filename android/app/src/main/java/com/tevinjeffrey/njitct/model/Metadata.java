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

import android.os.Parcel;
import android.os.Parcelable;

public class Metadata implements Parcelable{
    String title;
    String data;

    public Metadata(String title, String data) {
        this.title = title;
        this.data = data;
    }
    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    protected Metadata(Parcel in) {
        title = in.readString();
        data = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Metadata> CREATOR = new Parcelable.Creator<Metadata>() {
        @Override
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        @Override
        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };
}
