package com.tevinjeffrey.njitct.ui.chooser;

import com.tevinjeffrey.njitct.ui.base.StatefulPresenter;

public interface ChooserPresenter extends StatefulPresenter {
    void loadSystemMessage();
    boolean isLoading();
}
