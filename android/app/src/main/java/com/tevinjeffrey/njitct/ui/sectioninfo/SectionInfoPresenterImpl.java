package com.tevinjeffrey.njitct.ui.sectioninfo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.tevinjeffrey.rmp.common.Professor;
import com.tevinjeffrey.njitct.database.DatabaseHandler;
import com.tevinjeffrey.njitct.database.DatabaseUpdateEvent;
import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.model.TrackingApiModel;
import com.tevinjeffrey.njitct.ui.base.BasePresenter;
import com.tevinjeffrey.njitct.utils.AndroidMainThread;
import com.tevinjeffrey.njitct.utils.BackgroundThread;
import com.tevinjeffrey.njitct.utils.RxUtils;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;

public class SectionInfoPresenterImpl extends BasePresenter implements SectionInfoPresenter {

    private final String TAG = this.getClass().getSimpleName();

    @Inject
    DatabaseHandler mDatabaseHandler;

    @Inject
    TrackingApiModel mApi;

    @Inject
    Bus mBus;

    @Inject
    @AndroidMainThread
    Scheduler mMainThread;
    @Inject
    @BackgroundThread
    Scheduler mBackgroundThread;

    private final SectionModel mSection;

    private Subscription mSubscription;

    public SectionInfoPresenterImpl(SectionModel section) {
        this.mSection = section;
    }

    public void setFabState(boolean animate) {
        if (getView() != null) {
            boolean sectionTracked = mDatabaseHandler.isSectionTracked(mSection);
            getView().showSectionTracked(sectionTracked, animate);
        }
    }

    public void toggleFab() {
        boolean sectionTracked = mDatabaseHandler.isSectionTracked(mSection);
        if (sectionTracked) {
            removeSection(mSection);
        } else {
            addSection(mSection);
        }
    }


    @Override
    public void removeSection(SectionModel sectionModel) {
        mDatabaseHandler.removeSectionFromDb(sectionModel);
    }

    @Override
    public void addSection(SectionModel sectionModel) {
        mDatabaseHandler.addSectionToDb(sectionModel);
    }

    public void loadRMP() {
        if (getView() != null) {
            getView().hideRatingsLoading();
            getView().hideRatingsLayout();
        }

        cancePreviousSubscription();

        Subscriber<Professor> subscriber = new Subscriber<Professor>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Professor professor) {
                if (getView() != null)
                    getView().addRMPProfessor(professor);
            }
        };
/*

        mSubscription = buildSearchParameters(mSection)
                .flatMap(new Func1<Parameter, Observable<Professor>>() {
                    @Override
                    public Observable<Professor> call(Parameter parameter) {
                        return rmp.getProfessor(parameter);
                    }
                })
                */
/*//*
/Should need this to busness code.
                .doOnNext(new Action1<Professor>() {
                    @Override
                    public void call(Professor professor) {
                        for (final Iterator<Instructors> iterator = professorsNotFound.iterator(); iterator.hasNext(); ) {
                            Instructors i = iterator.next();
                            if (StringUtils.getJaroWinklerDistance(i.getLastName(), professor.getLastName()) > .70
                                    || StringUtils.getJaroWinklerDistance(i.getLastName(), professor.getFirstName()) > .70) {
                                iterator.remove();
                            }
                        }
                    }
                })*//*

                .subscribeOn(mBackgroundThread)
                .observeOn(mMainThread)
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        if (getView() != null) {
                            getView().showRatingsLayout();
                            getView().hideRatingsLoading();
                        }
                    }
                })
                .subscribe(subscriber);
*/

    }

    private void cancePreviousSubscription() {
        RxUtils.unsubscribeIfNotNull(mSubscription);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onResume() {
        mBus.register(this);
    }

    @Subscribe
    public void onDbUpdateEvent(DatabaseUpdateEvent event) {
        setFabState(true);
    }

    @Nullable
    public SectionInfoView getView() {
        return (SectionInfoView) super.getView();
    }

    @Override
    public String toString() {
        return TAG;
    }

}
