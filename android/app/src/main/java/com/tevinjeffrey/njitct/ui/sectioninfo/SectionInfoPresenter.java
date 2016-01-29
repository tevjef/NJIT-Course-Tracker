package com.tevinjeffrey.njitct.ui.sectioninfo;

import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.ui.base.StatefulPresenter;

public interface SectionInfoPresenter extends StatefulPresenter {

    void setFabState(boolean animate);

    void toggleFab();

    void loadRMP();

    void removeSection(SectionModel request);

    void addSection(SectionModel request);
}
