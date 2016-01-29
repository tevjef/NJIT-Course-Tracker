package com.tevinjeffrey.njitct.ui.course;

import com.tevinjeffrey.njitct.model.CourseModel;
import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.model.TrackingApiModel;
import com.tevinjeffrey.njitct.ui.base.BasePresenter;
import com.tevinjeffrey.njitct.ui.base.View;
import com.tevinjeffrey.njitct.utils.AndroidMainThread;
import com.tevinjeffrey.njitct.utils.BackgroundThread;
import com.tevinjeffrey.njitct.utils.RxUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

public class CoursePresenterImpl extends BasePresenter implements CoursePresenter {

    private static final String TAG = CoursePresenterImpl.class.getSimpleName();
    private final SubjectModel selectedSubject;

    @Inject
    TrackingApiModel mApi;
    @Inject
    @AndroidMainThread
    Scheduler mMainThread;
    @Inject
    @BackgroundThread
    Scheduler mBackgroundThread;

    private Subscription mSubscription;
    private boolean isLoading;

    public CoursePresenterImpl(SubjectModel subjectModel) {
        this.selectedSubject = subjectModel;
    }

    @Override
    public void loadCourses(boolean pullToRefresh) {
        if (getView() != null)
            getView().showLoading(pullToRefresh);

        cancePreviousSubscription();

        Subscriber<List<? extends CourseModel>> mSubscriber = new Subscriber<List<? extends CourseModel>>() {
            @Override
            public void onCompleted() {
                if (getView() != null)
                    getView().showLoading(false);
            }

            @Override
            public void onError(Throwable e) {
                //Removes the animated loading drawable
                if (getView() != null)
                    getView().showLoading(false);
                //Lets the view decide what to display depending on what type of exception it is.
                if (getView() != null)
                    getView().showError(e);
            }

            @Override
            public void onNext(List<? extends CourseModel> courseList) {
                if (getView() != null)
                    getView().setData(courseList);

                if (courseList.size() > 0) {
                    if (getView() != null)
                        getView().showLayout(View.LayoutType.LIST);
                }
            }
        };

        mSubscription = mApi.getCourses(selectedSubject)
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

    public CourseView getView() {
        return (CourseView) super.getView();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public String toString() {
        return TAG;
    }
}
