package com.github.bluecatlee.common.test.controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.bluecatlee.common.jackson.serializer.LongToStringSerializer;
import com.github.bluecatlee.common.test.bean.CommonResp;
import com.github.bluecatlee.common.test.service.DemoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SuppressWarnings("all")
public class TestController {

    @RequestMapping("test")
    public void test() {

    }

    @Data
    public static class Demo {

        @JsonSerialize(using= LongToStringSerializer.class)
        private Long id;

        @JsonSerialize(using= ToStringSerializer.class)
        private Long num;

        private Long x;
        private Long y;
    }


    @Autowired
    private DemoService demoService;

    @GetMapping(value = "test1", produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<Demo> test1() {
        Demo demo = new Demo();
        demo.setId(100L);
        demo.setNum(101L);
        demo.setX(102L);
        demo.setY(9007199254740993L);
        return new ResponseEntity<>(demo, HttpStatus.OK);
    }

    @GetMapping("test2")
    public CommonResp test2(CommonResp.Meta meta) {
        return demoService.query(meta);
    }

}
