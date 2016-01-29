package com.tevinjeffrey.njitct.ui.course;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.CourseModel;

import butterknife.ButterKnife;

public final class CourseVH extends RecyclerView.ViewHolder {

    private final View mParent;
    private final TextView mCourseTitle;
    private final TextView mSectionInfo;

    public static CourseVH newInstance(View parent) {

        TextView courseTitle = ButterKnife.findById(parent, R.id.list_item_title);
        TextView sectionInfo = ButterKnife.findById(parent, R.id.course_list_sections);

        return new CourseVH(parent, courseTitle, sectionInfo);
    }

    private CourseVH(View parent, TextView courseTitle, TextView sectionInfo) {
        super(parent);
        this.mParent = parent;
        this.mSectionInfo = sectionInfo;
        this.mCourseTitle = courseTitle;
    }

    public void setCourseTitle(CourseModel course) {
        mCourseTitle.setText(course.getNumber() + " | " + course.getName());
    }

    public void setSectionsInfo(CourseModel course) {
        mSectionInfo.setText(course.getOpenSections() + " open sections of " + course.getTotalSections());
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mParent.setOnClickListener(listener);
    }

}
