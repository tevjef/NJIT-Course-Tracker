package com.tevinjeffrey.njitct.ui.courseinfo;

import com.tevinjeffrey.njitct.ui.base.BaseToolbarView;
import com.tevinjeffrey.njitct.ui.base.View;

public interface CourseInfoView extends View, BaseToolbarView {
    String SELECTED_SECTION = "SELECTED_SECTION";

    void initHeaderView();

    void initRecyclerView();

    void initViews();
}
