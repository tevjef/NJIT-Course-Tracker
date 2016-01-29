package com.tevinjeffrey.njitct.database;

import com.orhanobut.hawk.Hawk;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.tevinjeffrey.njitct.model.SectionModel;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DatabaseHandlerImpl implements DatabaseHandler {

    private final Bus bus;

    public DatabaseHandlerImpl(Bus eventBus) {
        this.bus = eventBus;
    }

    @Override
    public void removeSectionFromDb(final SectionModel sectionModel) {
        Hawk.remove(String.valueOf(sectionModel.hashCode()));
        notifyOnRemoveListeners(sectionModel);
        TrackedSections.executeQuery("DELETE FROM TRACKED_SECTIONS WHERE SECTION_ID = ?", String.valueOf(sectionModel.hashCode()));
    }

    public Observable<List<SectionModel>> getAllSections() {
        return Observable.just(TrackedSections.listAll(TrackedSections.class))
                .flatMap(new Func1<List<TrackedSections>, Observable<List<SectionModel>>>() {
            @Override
            public Observable<List<SectionModel>> call(List<TrackedSections> trackedSectionses) {
                return Observable.from(trackedSectionses)
                        .flatMap(new Func1<TrackedSections, Observable<SectionModel>>() {
                            @Override
                            public Observable<SectionModel> call(TrackedSections trackedSections) {
                                return Observable.just(Hawk.<SectionModel>get(String.valueOf(trackedSections.getSectionId())));
                            }
                        }).toList();
            }
        });
    }

    private void notifyOnRemoveListeners(SectionModel sectionModel) {
        bus.post(new DatabaseUpdateEvent());
    }

    private void notifyOnAddListeners(SectionModel sectionModel) {
        bus.post(new DatabaseUpdateEvent());
    }

    public boolean isSectionTracked(SectionModel sectionModel) {
        return Hawk.contains(String.valueOf(sectionModel.hashCode()));
    }

    public void addSectionToDb(final SectionModel sectionModel) {
        TrackedSections sections = new TrackedSections(sectionModel.hashCode());
        sections.save();
        Hawk.putObservable(String.valueOf(sectionModel.hashCode()), sectionModel)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        notifyOnAddListeners(sectionModel);
                    }
                })
                .subscribe();
    }


    @Produce
    public DatabaseUpdateEvent produceUpdate() {
        return new DatabaseUpdateEvent();
    }

    public Observable<List<SectionModel>> getObservableSections() {
        return getAllSections();
    }

}
