package com.tevinjeffrey.njitct.ui.sectioninfo;

import com.tevinjeffrey.rmp.common.Professor;
import com.tevinjeffrey.njitct.ui.base.BaseToolbarView;
import com.tevinjeffrey.njitct.ui.base.View;

@SuppressWarnings("BooleanParameter")
public interface SectionInfoView extends View, BaseToolbarView {

    //Shows the fab and parameter determines if it should be animated or not.
    void showFab(boolean animate);

    //Toggle state of the FAB based on if the section is added to the database. There's is a another
    //parameter to determine if the state change should be animated.
    void showSectionTracked(boolean sectionIsAdded, boolean animateView);

    //It's default behavior is to be hidden when created.
    void showRatingsLayout();

    void hideRatingsLayout();
    //It's default behavior is to be shown when created.
    void hideRatingsLoading();

    void addErrorProfessor(String name);

    void addRMPProfessor(Professor professor);

    void initViews();

}
