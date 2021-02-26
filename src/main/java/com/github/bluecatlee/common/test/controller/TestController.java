package com.github.bluecatlee.common.test.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.bluecatlee.common.jackson.serializer.LongToStringSerializer;
import com.github.bluecatlee.common.test.bean.CommonResp;
import com.github.bluecatlee.common.test.service.DemoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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

    public static void main(String[] args) {
        byte[] arr = {60,63,120,109,108,32,118,101,114,115,105,111,110,61,34,49,46,48,34,32,101,110,99,111,100,105,110,103,61,34,71,66,50,51,49,50,34,32,115,116,97,110,100,97,108,111,110,101,61,34,121,101,115,34,32,63,62,60,84,88,62,60,82,69,81,85,69,83,84,95,83,78,62,50,49,48,50,50,53,48,49,50,48,48,48,49,60,47,82,69,81,85,69,83,84,95,83,78,62,60,67,85,83,84,95,73,68,62,57,57,57,57,57,57,57,57,57,60,47,67,85,83,84,95,73,68,62,60,85,83,69,82,95,73,68,62,49,48,48,48,48,49,60,47,85,83,69,82,95,73,68,62,60,80,65,83,83,87,79,82,68,62,120,120,120,60,47,80,65,83,83,87,79,82,68,62,60,84,88,95,67,79,68,69,62,53,87,49,48,48,49,60,47,84,88,95,67,79,68,69,62,60,76,65,78,71,85,65,71,69,62,67,78,60,47,76,65,78,71,85,65,71,69,62,60,84,88,95,73,78,70,79,62,60,82,69,77,49,62,-79,-72,-41,-94,49,60,47,82,69,77,49,62,60,82,69,77,50,62,-79,-72,-41,-94,50,60,47,82,69,77,50,62,60,47,84,88,95,73,78,70,79,62,60,47,84,88,62};
        System.out.println(arr);
        String str = null;
        try {
            str = new String(arr, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(str);

    }

    @PostMapping("/")
    public String test3(HttpServletRequest request) {
        String connection = request.getHeader("Connection");
        String contentType = request.getHeader("Content-Type");
        String contentLength = request.getHeader("Content-Length");
        System.out.println(connection);
        System.out.println(contentType);
        System.out.println(contentLength);

//        String s = ReadAsChars2(request);

        try {
//            int len = request.getContentLength();
//            ServletInputStream iii = request.getInputStream();
//            byte[] buffer = new byte[len];
//            iii.read(buffer, 0, len);

//            byte[] arr = new byte[buffer.length];
//            System.arraycopy(buffer, 0, arr,0, buffer.length);

//            System.out.println(buffer);
//            System.out.println(arr);

//            String s = new String(buffer);
//            System.out.println(s);
//            String s1 = new String(arr);
//            System.out.println(s1);


            String requestXml = request.getParameter("requestXml");
//            Object requestXml1 = request.getAttribute("requestXml");

            System.out.println(requestXml);
//            System.out.println(requestXml1);


            String gb2312 = URLDecoder.decode(requestXml, "GB2312");
            System.out.println(gb2312);


            String xx =  "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?> \n" +
                    "<TX> \n" +
                    "  <REQUEST_SN>请求序列码</REQUEST_SN> \n" +
                    "  <CUST_ID>商户号</CUST_ID> \n" +
                    "  <TX_CODE>5W1001</TX_CODE> \n" +
                    "  <RETURN_CODE>000000</RETURN_CODE> \n" +
                    "  <RETURN_MSG>返回码说明</RETURN_MSG> \n" +
                    "  <LANGUAGE>CN</LANGUAGE> \n" +
                    "  <TX_INFO> \n" +
                    "    <REM1>备注1</REM1> \n" +
                    "    <REM2>备注2</REM2> \n" +
                    "  </TX_INFO>   \n" +
                    "</TX> \n";


//            return new String(xx.getBytes("GB2312"));
            String result = URLEncoder.encode(xx, "GB2312");
            return result;

        } catch (Exception e) {

        }
        return null;
    }

    private static String ReadAsChars2(HttpServletRequest request) {
        InputStream is = null;
        try {
            is = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1;) {
                sb.append(new String(b, 0, n));
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
