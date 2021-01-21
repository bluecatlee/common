package com.github.bluecatlee.common.test.bean;

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