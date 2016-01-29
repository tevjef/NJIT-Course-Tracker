package com.tevinjeffrey.njitct.ui.subject;

import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.model.TrackingApiModel;
import com.tevinjeffrey.njitct.ui.base.BasePresenter;
import com.tevinjeffrey.njitct.ui.base.View;
import com.tevinjeffrey.njitct.utils.AndroidMainThread;
import com.tevinjeffrey.njitct.utils.BackgroundThread;
import com.tevinjeffrey.njitct.utils.RxUtils;
import com.tevinjeffrey.njitct.utils.SemesterUtils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

public class SubjectPresenterImpl extends BasePresenter implements SubjectPresenter {

    private static final String TAG = SubjectPresenter.class.getSimpleName();

    @Inject
    TrackingApiModel mApi;
    @Inject
    @AndroidMainThread
    Scheduler mMainThread;
    @Inject
    @BackgroundThread
    Scheduler mBackgroundThread;

    private Subscription mSubscription;
    private final SemesterUtils.Semester mSemester;
    private boolean isLoading;

    public SubjectPresenterImpl(SemesterUtils.Semester semester) {
        this.mSemester = semester;
    }

    @Override
    public void loadSubjects(boolean pullToRefresh) {
        if (getView() != null)
            getView().showLoading(pullToRefresh);

        cancePreviousSubscription();

        Subscriber<List<? extends SubjectModel>> mSubscriber = new Subscriber<List<? extends SubjectModel>>() {
            @Override
            public void onCompleted() {
                if (getView() != null)
                    getView().showLoading(false);
            }

            @Override
            public void onError(Throwable e) {
                //Lets the view decide what to display depending on what type of exception it is.
                if (getView() != null)
                    getView().showError(e);
                //Removes the animated loading drawable
                if (getView() != null) {
                    getView().showLoading(false);
                }

            }

            @Override
            public void onNext(List<? extends SubjectModel> subjectList) {
                if (getView() != null) {

                    getView().setData(subjectList);

                    if (subjectList.size() == 0)
                        getView().showError(new IOException());

                    if (subjectList.size() > 0)
                        getView().showLayout(View.LayoutType.LIST);
                }
            }
        };

        mSubscription = mApi.getSubjects(mSemester)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        isLoading = true;
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        isLoading = false;
                    }
                })
                .subscribeOn(mBackgroundThread)
                .observeOn(mMainThread)
                .subscribe(mSubscriber);
    }

    private void cancePreviousSubscription() {
        RxUtils.unsubscribeIfNotNull(mSubscription);
    }

    public SubjectView getView() {
        return (SubjectView) super.getView();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }
}
