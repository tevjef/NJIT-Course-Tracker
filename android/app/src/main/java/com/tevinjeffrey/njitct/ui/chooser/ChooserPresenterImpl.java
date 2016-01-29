package com.tevinjeffrey.njitct.ui.chooser;

import com.tevinjeffrey.njitct.ui.base.BasePresenter;
import com.tevinjeffrey.njitct.utils.AndroidMainThread;
import com.tevinjeffrey.njitct.utils.BackgroundThread;
import com.tevinjeffrey.njitct.utils.RxUtils;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscription;

public class ChooserPresenterImpl extends BasePresenter implements ChooserPresenter {

    private static final String TAG = ChooserPresenterImpl.class.getSimpleName();

    @Inject
    @AndroidMainThread
    Scheduler mMainThread;
    @Inject
    @BackgroundThread
    Scheduler mBackgroundThread;

    private Subscription mSubsciption;
    private boolean isLoading;

    public ChooserPresenterImpl() {
    }

    @Override
    public void loadSystemMessage() {

        cancePreviousSubscription();

    }

    private void cancePreviousSubscription() {
        RxUtils.unsubscribeIfNotNull(mSubsciption);
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    public ChooserView getView() {
        return (ChooserView) super.getView();
    }


}
