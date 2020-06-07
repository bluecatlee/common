package com.github.bluecatlee.common.rxjava.practice;


import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * 练习
 * Created by 胶布 on 2020/5/31.
 */
public class Practice {

    public static void p0() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext("Hello World");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });

        Observable.just("Hello World").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });

        Observable.just("Hello World").subscribe(System.out::println);  // 方法引用是简化版的lambda表达式
    }


    public static void p1() {
        Observable.just("Hello World").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("onComplete()");
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                System.out.println("subscribe");
            }
        });

        System.out.println("===========================================");

        Observable.just("Hello World").subscribe(System.out::println,
                throwable -> System.out.println(throwable.getMessage()),
                () -> System.out.println("onComplete()"),
                disposable -> System.out.println("subscribe")
        );

        System.out.println("==========================================");

        Observable.just("Hello World").subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                System.out.println("subscribe");
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete()");
            }
        });

    }

    public static void p2() {
        Observable.just("Hello")
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        System.out.println("doOnNext: " + s);
                    }
                })
                .doAfterNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        System.out.println("doAfterNext: " + s);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doOnComplete: ");
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        System.out.println("doOnSubscribe: ");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doAfterTerminate: ");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doFinally: ");
                    }
                })
                .doOnEach(new Consumer<Notification<String>>() {
                    @Override
                    public void accept(Notification<String> stringNotification) throws Exception {
                        System.out.println("doOnEach: " + (stringNotification.isOnNext() ? "onNext" : stringNotification.isOnComplete() ? "onComplete" : "onError"));
                    }
                })
                .doOnLifecycle(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("doOnLifecycle: " + disposable.isDisposed());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doOnLifecycle run: ");
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("收到消息： " + s);
                    }
                });

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        Observable.just("Hello World").doOnNext(s -> System.out.println("doOnNext: " + s))
                                      .doAfterNext(s -> System.out.println("doAfterNext: " + s))
                                      .doOnComplete(() -> System.out.println("doOnComplete: "))
                                      .doOnSubscribe(disposable -> System.out.println("doOnSubscribe: "))
                                      .doAfterTerminate(() -> System.out.println("doAfterTerminate: "))
                                      .doFinally(() -> System.out.println("doFinally: "))
                                      .doOnEach(stringNotification -> System.out.println("doOnEach: "
                                              + (stringNotification.isOnNext() ? "onNext" : stringNotification.isOnComplete() ? "onComplete" : "onError")))
                                      .doOnLifecycle(disposable -> System.out.println("doOnLifecycle: " + disposable.isDisposed()),
                                                     () -> System.out.println("doOnLifecycle run: "))
                                      .subscribe(s -> System.out.println("收到消息: " + s));


    }

    public static void p3() {
//        Consumer<Long> subscriber1 = new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                System.out.println("sunscribe1: " + aLong);
//            }
//        };
//        Consumer<Long> subscriber2 = new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                System.out.println("    sunscribe2: " + aLong);
//            }
//        };
//        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
//            @Override
//            public void subscribe(ObservableEmitter<Long> observableEmitter) throws Exception {
//                Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.computation())
//                        .take(Integer.MAX_VALUE).subscribe(observableEmitter::onNext);
//            }
//        }).observeOn(Schedulers.newThread());
//        observable.subscribe(subscriber1);
//        observable.subscribe(subscriber2);
//
//        try {
//            Thread.sleep(100L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("--------------------------------------------------------------------");

        Observable<Object> observable2 = Observable.create(observableEmitter -> Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.computation())
                .take(Integer.MAX_VALUE).subscribe(observableEmitter::onNext))
                .observeOn(Schedulers.newThread());
        observable2.subscribe(aLong -> System.out.println("subscribe1: " + aLong));
        observable2.subscribe(aLong -> System.out.println(" subscribe2: " + aLong));
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
//        p0();
//        p1();
//        p2();
        p3();
    }

}
