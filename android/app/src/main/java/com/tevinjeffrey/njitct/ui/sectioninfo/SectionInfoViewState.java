package com.tevinjeffrey.njitct.ui.sectioninfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.tevinjeffrey.njitct.ui.base.BaseViewState;

public class SectionInfoViewState extends BaseViewState<SectionInfoView> implements Parcelable {

    public boolean shouldAnimateFabIn = true;
    public boolean isSectionAdded = false;

    @Override
    public void apply(SectionInfoView view, boolean retainedState) {
        view.initToolbar();
        view.initViews();
        view.showFab(shouldAnimateFabIn);

    }

    public SectionInfoViewState() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(shouldAnimateFabIn ? (byte) 1 : (byte) 0);
        dest.writeByte(isSectionAdded ? (byte) 1 : (byte) 0);
    }

    protected SectionInfoViewState(Parcel in) {
        this.shouldAnimateFabIn = in.readByte() != 0;
        this.isSectionAdded = in.readByte() != 0;
    }

    public static final Creator<SectionInfoViewState> CREATOR = new Creator<SectionInfoViewState>() {
        public SectionInfoViewState createFromParcel(Parcel source) {
            return new SectionInfoViewState(source);
        }

        public SectionInfoViewState[] newArray(int size) {
            return new SectionInfoViewState[size];
        }
    };
}
