package com.tevinjeffrey.njitct.ui.course;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.CourseModel;
import com.tevinjeffrey.njitct.ui.utils.ItemClickListener;

import java.util.List;

public class CourseFragmentAdapter extends RecyclerView.Adapter<CourseVH> {

    private List<? extends CourseModel> courseList;
    private ItemClickListener<CourseModel, View> itemClickListener;

    public CourseFragmentAdapter(List<? extends CourseModel> courseList, @NonNull ItemClickListener<CourseModel, View> listener) {
        this.courseList = courseList;
        this.itemClickListener = listener;
    }

    @Override
    public CourseVH onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();
        final View parent = LayoutInflater.from(context).inflate(R.layout.course_list_item, viewGroup, false);

        return CourseVH.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(final CourseVH holder, int position) {

        final CourseModel course = courseList.get(position);

        holder.setCourseTitle(course);
        holder.setSectionsInfo(course);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(course, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

}
