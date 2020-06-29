package com.github.bluecatlee.common.third.shunfeng.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SfProductDetail extends SfProduct {

    /**
     * 图文详情图片地址 非必回
     */
    private List<String> imgTxtDetail;

    @JsonProperty("imgAddress")
    private List<String> images;

}
