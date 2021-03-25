package com.github.bluecatlee.common.negative.examples.bean;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CommonResp<T> {
    private T data;
    private Integer returnCode = 0;
    private String returnMessage = "OK";
    private Meta meta;                                      // Meta中的page和pageSize字段作为入参使用 total作为出参使用 同样不符合面向对象编程

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