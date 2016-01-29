package com.tevinjeffrey.njitct.ui.subject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.ui.utils.ItemClickListener;

import java.util.List;

public class SubjectFragmentAdapter extends RecyclerView.Adapter<SubjectVH> {

    private final List<SubjectModel> subjectList;
    private final ItemClickListener<SubjectModel, View> itemClickListener;

    public SubjectFragmentAdapter(List<SubjectModel> subjectList, @NonNull ItemClickListener<SubjectModel, View> listener) {
        this.subjectList = subjectList;
        this.itemClickListener = listener;
    }

    @Override
    public SubjectVH onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();
        final View parent = LayoutInflater.from(context).inflate(R.layout.subject_list_item, viewGroup, false);

        return SubjectVH.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(final SubjectVH holder, int position) {

        final SubjectModel subject = subjectList.get(position);

        holder.setSubjectTitle(subject);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(subject, v);
            }
        });
    }


    @Override
    public int getItemCount() {
        return subjectList.size();
    }

}
