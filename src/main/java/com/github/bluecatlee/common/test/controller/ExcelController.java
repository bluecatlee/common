package com.github.bluecatlee.common.test.controller;

import com.github.bluecatlee.common.excel.impl2.BaseExcelUtil;
import com.github.bluecatlee.common.test.bean.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @GetMapping("/download")
    public void test(HttpServletRequest request, HttpServletResponse response) {
        User user1 = new User("蓝猫", 20);
        User user2 = new User("淘气", 18);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        String[] titleName = {"姓名", "年龄"};
//        String[] titleField = {"name", "age"};
        String[] titleField = {"name", "age@"};

        Map<String, String[]> header = new HashMap<>();
        header.put("name", new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I"});
        header.put("age@", new String[]{"0-9","10-19","20-29","30-39","40-49","50-59","60-69","70-79","80-89","90-99","100-109","110-119","120-129"});  // demo不合理 这个其实是做映射枚举值含义的

//        Boolean aBoolean = BaseExcelUtil.buildModel(users, titleName, titleField, null, "人员", "03", response);

        Boolean x = BaseExcelUtil.buildModel(users, titleName, titleField, header, "人员2", "2007", response);

    }

}
