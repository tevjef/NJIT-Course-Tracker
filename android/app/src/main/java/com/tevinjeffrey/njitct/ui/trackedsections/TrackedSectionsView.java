package com.tevinjeffrey.njitct.ui.trackedsections;

import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.ui.base.BaseToolbarView;
import com.tevinjeffrey.njitct.ui.base.View;

import java.util.List;

@SuppressWarnings("BooleanParameter")
public interface TrackedSectionsView extends View, BaseToolbarView {

    String REQUEST = "REQUEST";
    String SEMESTER = "SEMESTER";

    void showLoading(boolean pullToRefresh);

    void setData(List<? extends SectionModel> data);

    void showError(Throwable e);

    void showLayout(LayoutType showEmptyLayout);

    void initRecyclerView();

    void initSwipeLayout();
}
