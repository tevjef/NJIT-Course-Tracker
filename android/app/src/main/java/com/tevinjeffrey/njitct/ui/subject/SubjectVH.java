package com.tevinjeffrey.njitct.ui.subject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.SubjectModel;

import org.apache.commons.lang3.text.WordUtils;

import butterknife.ButterKnife;

public final class SubjectVH extends RecyclerView.ViewHolder {

    private final View mParent;
    private final TextView mSubjectTitle;

    public static SubjectVH newInstance(View parent) {

        TextView subjectTitle = ButterKnife.findById(parent, R.id.list_item_title);

        return new SubjectVH(parent, subjectTitle);
    }

    public SubjectVH(View parent, TextView subjectTitle) {
        super(parent);
        this.mParent = parent;
        this.mSubjectTitle = subjectTitle;
    }

    public void setSubjectTitle(SubjectModel subject) {
        String text = subject.getName();
        if (text != null) {
            mSubjectTitle.setText(WordUtils.capitalize(text.toLowerCase()));
        } else {
            mSubjectTitle.setText(WordUtils.capitalize(subject.getNumber()));
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mParent.setOnClickListener(listener);
    }

}
