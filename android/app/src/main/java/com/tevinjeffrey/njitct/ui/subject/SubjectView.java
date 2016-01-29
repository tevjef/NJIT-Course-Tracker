package com.tevinjeffrey.njitct.ui.subject;

import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.ui.base.BaseToolbarView;
import com.tevinjeffrey.njitct.ui.base.View;

import java.util.List;

public interface SubjectView extends View, BaseToolbarView {

    String SELECTED_SUBJECT = "SELECTED_SUBJECT";

    void showLoading(boolean pullToRefresh);

    void setData(List<? extends SubjectModel> data);

    void showError(Throwable e);

    void showLayout(LayoutType showEmptyLayout);

    void initRecyclerView();

    void initSwipeLayout();
}
