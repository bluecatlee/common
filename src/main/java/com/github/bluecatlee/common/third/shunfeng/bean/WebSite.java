package com.github.bluecatlee.common.third.shunfeng.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 站点
 */
@Data
public class WebSite {

    /**
     * 站点编号
     */
    @JsonProperty("siteNo")
    private Integer webSiteNo;

    /**
     * 站点名称
     */
    @JsonProperty("siteName")
    private String webSiteName;

    /**
     * 所有运营大类
     */
    private List<CateOneLevel> operCateOneLeves;

}
