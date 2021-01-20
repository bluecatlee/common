package com.github.bluecatlee.common.mybatis.tk;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础service
 *      将tkMybatis的通用方法二次封装到service层
 * @param <T>
 * @param <V>
 */
@Slf4j
@Deprecated
public class BaseService<T, V> {

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class CommonResp<T> {
        private T data;
        private Integer returnCode = 0;
        private String returnMessage = "OK";
        private Meta meta;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        static public class Meta {
            public static final Meta ONE = Meta.builder().page(1).pageSize(1).build();
            public static final Meta ALL = Meta.builder().page(1).pageSize(10000).build();
            Integer page = 1;
            Integer pageSize = 10;
            Long total;
        }
    }

    @Autowired
    private Mapper<T> mapper;

    public CommonResp<List<V>> queryList(Example example, CommonResp.Meta meta, Class<V> cv) {
        CommonResp<List<V>> resp = new CommonResp<>();

//        if(StringUtils.isBlank(example.getOrderByClause())){
//            example.setOrderByClause("series desc");
//        }

        PageHelper.startPage(meta.getPage(), meta.getPageSize());
        List<T> dbData = mapper.selectByExample(example);
        PageInfo<T> pageInfo = new PageInfo<>(dbData);
        List<V> data = new ArrayList<>();
        if(dbData != null && dbData.size() > 0) {
            data = dbData.stream().map(x -> {
                try {
                    V vo = cv.newInstance();
                    BeanUtils.copyProperties(x, vo);
                    return vo;
                } catch (Exception e) {
                    log.error(" T newInstance ", e);
                }
                return null;
            }).collect(Collectors.toList());
        }
        resp.setData(data);
        meta.setTotal(pageInfo.getTotal());
        resp.setMeta(meta);
        return resp;
    }

    public CommonResp add(T query) {
        int i = mapper.insertSelective(query);
        CommonResp commonResp = new CommonResp();
        commonResp.setData(i);
        return commonResp;
    }

    public CommonResp modifyByKey(T query) {
        int i = mapper.updateByPrimaryKeySelective(query);
        CommonResp commonResp = new CommonResp();
        commonResp.setData(i);
        return commonResp;
    }

}
