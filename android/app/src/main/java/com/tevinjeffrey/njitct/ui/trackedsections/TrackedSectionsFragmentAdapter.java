package com.tevinjeffrey.njitct.ui.trackedsections;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.ui.utils.ItemClickListener;

import java.util.List;

public class TrackedSectionsFragmentAdapter extends RecyclerView.Adapter<TrackedSectionVH> {

    private final List<SectionModel> sectionList;
    private final ItemClickListener<SectionModel, View> itemClickListener;

    public TrackedSectionsFragmentAdapter(List<SectionModel> sectionList, @NonNull ItemClickListener<SectionModel, View> listener) {
        this.sectionList = sectionList;
        this.itemClickListener = listener;
    }

    @Override
    public TrackedSectionVH onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();
        final View parent = LayoutInflater.from(context).inflate(R.layout.section_layout_with_title, viewGroup, false);

        return TrackedSectionVH.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(final TrackedSectionVH holder, int position) {

        final SectionModel section = sectionList.get(position);

        holder.setCourseTitle(section.getCourse());
        holder.setOpenStatus(section);
        holder.setSectionNumber(section);
        holder.setInstructors(section);
        holder.setTimes(section);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(section, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }
}
