package com.github.bluecatlee.common.hystrix;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务请求合并命令
 * Created by 胶布 on 2020/6/13.
 */
public class ServiceRequestCollapseCommand<T, P> extends HystrixCollapser<List<T>, T, P>{

    private BatchService batchService;
    private P param;

    public ServiceRequestCollapseCommand(BatchService batchService, P param) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey(batchService.getClass().getSimpleName()))
                    .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
        this.batchService = batchService;
        this.param = param;
    }

    @Override
    public P getRequestArgument() {
        return param;
    }

    @Override
    protected HystrixCommand<List<T>> createCommand(Collection<CollapsedRequest<T, P>> collapsedRequests) {
        List<P> params = new ArrayList<>(collapsedRequests.size());
        params.addAll(collapsedRequests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
        return new ServiceRequestBatchCommand(batchService, params);
    }

    @Override
    protected void mapResponseToRequests(List<T> batchResponse, Collection<CollapsedRequest<T, P>> collapsedRequests) {
        int count = 0;
        for (CollapsedRequest<T, P> collapsedRequest : collapsedRequests) {
            T t = batchResponse.get(count++);
            collapsedRequest.setResponse(t);
        }
    }

}
