package com.tevinjeffrey.njitct.ui.trackedsections;

import com.tevinjeffrey.njitct.ui.base.StatefulPresenter;

public interface TrackedSectionsPresenter extends StatefulPresenter {
    //Couplped showing the loading animation with loading the View's data.
    void loadTrackedSections(boolean pullToRefresh);
    //I little utility method to determine if the Presenter is doing any work.
    boolean isLoading();
}
