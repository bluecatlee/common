package com.github.bluecatlee.common.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SuppressWarnings("all")
public class TestController {

    @GetMapping("test")
    public void test() {

    }



}
