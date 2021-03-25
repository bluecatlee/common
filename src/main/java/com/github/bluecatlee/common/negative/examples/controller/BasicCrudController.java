package com.github.bluecatlee.common.negative.examples.controller;

import com.github.bluecatlee.common.negative.examples.bean.CommonResp;
import com.github.bluecatlee.common.negative.examples.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

/**
 * 通用的基于tkMybatis的增删改查控制器
 * @param <Q>   查询参数
 * @param <D>   删除操作的查询参数
 * @param <T>   写数据
 */
@RestController
@Slf4j
public abstract class BasicCrudController<Q, D, T> {

    @Autowired
    protected BaseService<T> service;

    @GetMapping("/query")
    public CommonResp query(Q query, CommonResp.Meta meta) {
        return service.queryList(query, getExample(), meta);
    }

    @PostMapping("/add")
    public CommonResp add(T query) {
        return service.add(query);
    }

    @PostMapping("/modify")
    public CommonResp modify(T query) {
        return service.modify(query, getExample());
    }

    @RequestMapping(value = "/remove", method = {RequestMethod.POST, RequestMethod.GET})
    public CommonResp remove(@Validated D query) {
        return service.remove(query, getExample());
    }

    protected abstract Class<T> getT();

    protected Example getExample() {
        return new Example(getT());
    }

}
