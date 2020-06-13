package com.github.bluecatlee.common.hystrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 胶布 on 2020/6/13.
 */
public class Test {

    static class TestService implements BatchService.SimpleBatchService {

        @Override
//        @HystrixCommand(commandKey = "TestService")
        public String exec(Long id) {
            return String.valueOf(id);
        }

        @Override
//        @HystrixCommand
        public List<String> exec(List<Long> params) {
            System.out.println("批处理方法执行");
            return params.stream().map(String::valueOf).collect(Collectors.toList());
        }
    }

    public static void main(String[] args){
        TestService testService = new TestService();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        ServiceRequestCollapseCommand serviceRequestCollapseCommand = new ServiceRequestCollapseCommand(new TestService(), list);
        serviceRequestCollapseCommand.execute();  // todo

    }


}
