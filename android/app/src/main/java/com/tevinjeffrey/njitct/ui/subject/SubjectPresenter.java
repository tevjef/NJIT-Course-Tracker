package com.tevinjeffrey.njitct.ui.subject;

import com.tevinjeffrey.njitct.ui.base.StatefulPresenter;

public interface SubjectPresenter extends StatefulPresenter {
    //When reacting to a
    void loadSubjects(boolean pullToRefresh);

    boolean isLoading();
}
