/*
 * Copyright 2015 Tevin Jeffrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tevinjeffrey.njitct.njitapi;

import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.model.TrackingApiModel;
import com.tevinjeffrey.njitct.utils.SemesterUtils;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class RetroNjit implements TrackingApiModel {

    private final RetroNjitService retroNjitService;

    public RetroNjit(RetroNjitService retroNjitService) {
        this.retroNjitService = retroNjitService;
    }

    @Override
    public Observable<NSection> getTrackedSections(List<SectionModel> allSections) {
        return Observable.from(allSections)
                .flatMap(new Func1<SectionModel, Observable<NSection>>() {
                    @Override
                    public Observable<NSection> call(final SectionModel sectionModel) {
                        return retroNjitService.getSection(sectionModel.getCourse().getSubject().getSemester().toNjitString(), sectionModel.getIndexNumber())
                                .map(new Func1<NSection, NSection>() {
                                    @Override
                                    public NSection call(NSection nSection) {
                                        nSection.setCourse(sectionModel.getCourse());
                                        return nSection;
                                    }
                                });
                    }
                });
    }

    @Override
    public Observable<List<NSubject>> getSubjects(SemesterUtils.Semester semester) {
        return retroNjitService.getSubjects(semester.toNjitString());
    }

    @Override
    public Observable<List<NCourse>> getCourses(final SubjectModel selectedSubject) {

        return  getSubjects(selectedSubject.getSemester())
                .flatMap(new Func1<List<NSubject>, Observable<NSubject>>() {
            @Override
            public Observable<NSubject> call(List<NSubject> nSubjects) {
                return Observable.from(nSubjects).filter(new Func1<NSubject, Boolean>() {
                    @Override
                    public Boolean call(NSubject nSubject) {
                        return nSubject.getNumber().equals(selectedSubject.getNumber());
                    }
                }).map(new Func1<NSubject, NSubject>() {
                    @Override
                    public NSubject call(NSubject nSubject) {
                        nSubject.setSemester(selectedSubject.getSemester());
                        return  nSubject;
                    }
                });
            }
        })
                .flatMap(new Func1<NSubject, Observable<List<NCourse>>>() {
                    @Override
                    public Observable<List<NCourse>> call(final NSubject nSubject) {
                        return retroNjitService.getCourses(selectedSubject.getSemester().toNjitString(), selectedSubject.getNumber())
                                .flatMap(new Func1<List<NCourse>, Observable<List<NCourse>>>() {
                                    @Override
                                    public Observable<List<NCourse>> call(List<NCourse> nCourses) {
                                        return Observable.from(nCourses).map(new Func1<NCourse, NCourse>() {
                                            @Override
                                            public NCourse call(NCourse nCourse) {
                                                nCourse.setSubject(nSubject);
                                                for (NSection section : nCourse.sections) {
                                                    NCourse course = new NCourse(nCourse);
                                                    section.setCourse(course);
                                                }
                                                return nCourse;
                                            }
                                        }).toList();
                                    }
                                });
                    }
                });
    }

}
