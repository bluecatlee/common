package com.github.bluecatlee.common.rxjava.eventbus;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by 胶布 on 2020/6/6.
 */
public class RxBus2 {

    // PublishProcessor支持背压
    private final FlowableProcessor<Object> mBus;

    private RxBus2() {
        mBus = PublishProcessor.create().toSerialized();
    }

    private static class Holder {
        private static final RxBus2 BUS = new RxBus2();  // 利用类初始化一次的特性来保证单例
    }

    public static RxBus2 get() {
        return Holder.BUS;
    }

    // 发送事件
    public void post(Object obj) {
        mBus.onNext(obj);
    }

    // 将某个类型的事件转成Flowable
    public <T> Flowable<T> toFlowable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Flowable<Object> toFlowable() {
        return mBus;
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

}
