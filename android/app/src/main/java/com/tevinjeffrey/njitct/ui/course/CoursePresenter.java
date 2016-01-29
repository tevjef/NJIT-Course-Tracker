package com.tevinjeffrey.njitct.ui.course;

import com.tevinjeffrey.njitct.ui.base.StatefulPresenter;

public interface CoursePresenter extends StatefulPresenter {
    void loadCourses(boolean pullToRefresh);
    boolean isLoading();
}
