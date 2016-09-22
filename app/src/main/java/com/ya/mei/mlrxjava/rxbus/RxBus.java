package com.ya.mei.mlrxjava.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by chenliang3 on 2016/3/14.
 */
public class RxBus {

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o){
        bus.onNext(o);
    }

    public Observable<Object> toObserverable(){
        return bus;
    }

    public boolean hasObservers(){
        return bus.hasObservers();
    }

}
