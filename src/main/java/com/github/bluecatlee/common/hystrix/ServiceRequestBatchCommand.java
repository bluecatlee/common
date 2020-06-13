package com.github.bluecatlee.common.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 服务请求批处理命令
 * Created by 胶布 on 2020/6/13.
 */
public class ServiceRequestBatchCommand<T> extends HystrixCommand<List<T>> {

    private BatchService batchService;
    private List params;

    protected ServiceRequestBatchCommand(BatchService batchService, List params) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(batchService.getClass().getSimpleName()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(batchService.getClass() + "-Batch")));
        this.batchService = batchService;
        this.params = params;
    }

    @Override
    protected List<T> run() throws Exception {
        Assert.notNull(batchService, "service must not be null");
        Assert.notEmpty(params, "batch method params must not be null");
        return batchService.batch(params);
    }

    @Override
    protected List<T> getFallback() {
        return super.getFallback();
    }

}
