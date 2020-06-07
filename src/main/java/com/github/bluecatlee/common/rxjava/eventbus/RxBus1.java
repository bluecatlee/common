package com.github.bluecatlee.common.rxjava.eventbus;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by 胶布 on 2020/6/6.
 */
public class RxBus1 {

    // Subject既是Observable又是Observer，不支持背压
    private final Subject<Object> mBus;

    private RxBus1() {
        mBus = PublishSubject.create().toSerialized();
    }

    private static class Holder {
        private static final RxBus1 BUS = new RxBus1();  // 利用类初始化一次的特性来保证单例
    }

    public static RxBus1 get() {
        return Holder.BUS;
    }

    // 发送事件
    public void post(Object obj) {
        mBus.onNext(obj);
    }

    // 将某个类型的事件转成Observable
    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

}
