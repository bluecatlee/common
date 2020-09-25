package com.github.bluecatlee.common.async;

import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 自定义带异常处理的异步任务执行器
 *      如果只是简单的异步处理 直接使用@Async配合@EnableAsync即可
 *      一个方法上同时使用@Async和@Transactional会导致事务失效
 */
public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor {

    private AsyncTaskExecutor executor;

    public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor executor) {
         this.executor = executor;
    }

    @Override
    public void execute(Runnable runnable) {
        executor.execute(wrappedRunnable(runnable));
    }

    @Override
    public void execute(Runnable runnable, long timeout) {
        executor.execute(wrappedRunnable(runnable), timeout);
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        return executor.submit(wrappedRunnable(runnable));
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(wrappedCallable(callable));
    }

    /**
     * 包装Runnable任务
     * @param task
     * @return
     */
    private Runnable wrappedRunnable(final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    handle(e);
                    // 不需要抛出 因为runnable方式外层无法获取返回值，无法捕获异常
                    // throw e;
                }
            }
        };
    }

    /**
     * 包装Callable任务
     * @param task
     * @return
     */
    private Callable wrappedCallable(final Callable task) {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                try {
                    return task.call();
                } catch (Exception e) {
                    handle(e);
                    throw e;
                }
            }
        };
    }

    /**
     * 自定义异常处理逻辑
     * @param e
     */
    private void handle(Exception e) {
        //
    }

}
