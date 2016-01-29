package com.tevinjeffrey.njitct.ui.courseinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.ui.utils.HeaderVH;
import com.tevinjeffrey.njitct.ui.utils.ItemClickListener;

import java.util.List;

public class CourseInfoFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<? extends SectionModel> sectionList;
    final private ItemClickListener<SectionModel, View> itemClickListener;
    final private List<View> mHeaders;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public CourseInfoFragmentAdapter(List<View> headers, List<? extends SectionModel> sections, @NonNull CourseInfoFragment listener) {
        this.mHeaders = headers;
        this.itemClickListener = listener;
        this.sectionList = sections;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();

        if (viewType == TYPE_HEADER) {
            final LinearLayout parent = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.course_info_metadata_container, viewGroup, false);
            return HeaderVH.newInstance(parent);
        } else if (viewType == TYPE_ITEM) {
            final View parent = LayoutInflater.from(context).inflate(R.layout.section_layout, viewGroup, false);
            return CourseInfoVH.newInstance(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER && position == 0) {
            HeaderVH headerVH = (HeaderVH) holder;
            headerVH.setHeaders(mHeaders);
        } else if (holder.getItemViewType() == TYPE_ITEM) {
            CourseInfoVH courseInfoVH = (CourseInfoVH) holder;
            final SectionModel section = sectionList.get(position - 1);

            courseInfoVH.setOpenStatus(section);
            courseInfoVH.setSectionNumber(section);
            courseInfoVH.setInstructors(section);
            courseInfoVH.setTimes(section);

            courseInfoVH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(section, v);
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public long getItemId(int position) {
        if (position != 0)
            return Long.valueOf(sectionList.get(position - 1).getIndexNumber());
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return sectionList.size() + 1;
    }
}
