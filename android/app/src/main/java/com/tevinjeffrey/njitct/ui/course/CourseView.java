package com.tevinjeffrey.njitct.ui.course;

import com.tevinjeffrey.njitct.model.CourseModel;
import com.tevinjeffrey.njitct.ui.base.BaseToolbarView;
import com.tevinjeffrey.njitct.ui.base.View;

import java.util.List;

@SuppressWarnings("BooleanParameter")
public interface CourseView extends BaseToolbarView, View {

    String SELECTED_COURSE = "SELECTED_COURSE";

    void showLoading(boolean pullToRefresh);

    void setData(List<? extends CourseModel> data);

    void showError(Throwable e);

    //Shows either the default LIST layout, ERROR or EMPTY layout.
    void showLayout(LayoutType showEmptyLayout);

    void initRecyclerView();

    void initSwipeLayout();
}
