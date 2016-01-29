package com.tevinjeffrey.njitct.ui.trackedsections;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.CourseModel;
import com.tevinjeffrey.njitct.ui.sectioninfo.SectionInfoVH;
import com.tevinjeffrey.njitct.ui.utils.CircleView;

import butterknife.ButterKnife;

public final class TrackedSectionVH extends SectionInfoVH {

    private final TextView mCourseTitleText;

    public static TrackedSectionVH newInstance(View parent) {
        SectionInfoVH sectionInfoVH = SectionInfoVH.newInstance(parent);
        TextView courseTitleText = ButterKnife.findById(parent, R.id.course_title_text);
        TextView instructors = sectionInfoVH.mInstructors;
        CircleView sectionNumberBackground = sectionInfoVH.mSectionNumberBackground;
        ViewGroup sectionTimeContainer = sectionInfoVH.mSectionTimeContainer;
        return new TrackedSectionVH(parent, instructors, courseTitleText, sectionNumberBackground, sectionTimeContainer);
    }

    private TrackedSectionVH(View parent, TextView instructors, TextView courseTitleText, CircleView sectionNumberBackground, ViewGroup mSectionTimeContainer) {
        super(parent, instructors, sectionNumberBackground, mSectionTimeContainer);
        this.mCourseTitleText = courseTitleText;
    }

    public void setCourseTitle(CourseModel course) {
        mCourseTitleText.setText(course.getSubject().getNumber() + " | " + course.getName());
    }
}
