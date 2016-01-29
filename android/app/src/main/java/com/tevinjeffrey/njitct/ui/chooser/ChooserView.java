package com.tevinjeffrey.njitct.ui.chooser;

import com.tevinjeffrey.njitct.ui.base.BaseToolbarView;
import com.tevinjeffrey.njitct.ui.base.View;
import com.tevinjeffrey.njitct.utils.SemesterUtils.Semester;

public interface ChooserView extends BaseToolbarView, View {
    void initPicker();
    void restoreOtherSemester(String otherSemesterText, Semester otherSemesterTag);
}