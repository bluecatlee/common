package com.github.bluecatlee.common.negative.examples.service;


import com.alibaba.fastjson.JSONObject;
import com.github.bluecatlee.common.bean.BeanUtil;
import com.github.bluecatlee.common.negative.examples.bean.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.joor.Reflect.on;

@Slf4j
public class BaseService<T> {

    @Autowired
    public Mapper<T> mapper;

    public CommonResp<List<T>> queryList(Object query, Example o, CommonResp.Meta meta) {
        int offset = (meta.getPage() - 1) * meta.getPageSize();
        int limit = meta.getPageSize();
        RowBounds rowBound = new RowBounds(offset, limit);
        CommonResp<List<T>> objectCommonResp = new CommonResp<>();
        List<T> ts = mapper.selectByExampleAndRowBounds(buildExample(query, o), rowBound);
        int i = mapper.selectCountByExample(buildExample(query, o));
        meta = CommonResp.Meta.builder().total(i + 0L).page(meta.getPage()).pageSize(meta.getPageSize()).build();
        objectCommonResp.setData(ts);
        objectCommonResp.setMeta(meta);
        return objectCommonResp;
    }

    public CommonResp add(T query) {
        int i = mapper.insertSelective(query);
        CommonResp commonResp = new CommonResp();
        commonResp.setData(i);
        return commonResp;
    }

    public CommonResp modify(T query, Example example) {
        int i = mapper.updateByPrimaryKeySelective(query);
        CommonResp commonResp = new CommonResp();
        commonResp.setData(i);
        return commonResp;
    }

    public CommonResp remove(Object query, Example example) {
        Map<String, Object> beanMap = BeanUtil.convertBean(query);
        int delete = 0;
        JSONObject queryMap = new JSONObject();
        //逗号分隔的列表要拆成list传入
        beanMap.forEach((k, v) -> {
            if (v.toString().contains(",")) {
                v = Arrays.asList(v.toString().split(","));
            }
            queryMap.put(k, v);
        });
        if (beanMap.size() == 0) {
            //无查询条件删除时候要报错
            throw new RuntimeException("error!!! This is trying to delete full table!!!");
        }
        delete = mapper.deleteByExample(buildExample(queryMap, example));
        CommonResp commonResp = new CommonResp();
        commonResp.setData(delete);
        return commonResp;
    }

    private Example buildExample(Object obj, Example example) {
        assert obj != null;
        Example.Criteria and = example.and();
        final Example.Criteria[] criteria = {and};

        if (obj instanceof Map) {
            Map map = (Map)obj;
            map.forEach((k, v) -> {
                if (v == null) {
                    return;
                }
                if (v instanceof List) {
                    List list = (List)v;
                    if (list.isEmpty()) {
                        return;
                    }
                    criteria[0] = criteria[0].andIn(k + "", list);
                    return;
                }
                criteria[0] = criteria[0].andEqualTo(k + "", v);
            });
        } else {
            on(obj).fields().forEach((x, y) -> {
                if (y.get() == null) {
                    return;
                }
                criteria[0] = criteria[0].andEqualTo(x, y.get());
            });
        }
        example.setOrderByClause("series desc");
        return example;
    }

}