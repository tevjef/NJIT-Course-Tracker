package com.tevinjeffrey.njitct.utils;

import rx.Observable;

public interface SchedulerTransformer<T> extends Observable.Transformer<T, T> {
}