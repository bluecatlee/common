package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

/**
 * 小站覆盖范围
 */
@Data
public class CoverScope {

    /**
     * 省名称 必回
     */
    private String provName;

    /**
     * 省编号
     */
    private Integer provSysNo;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 市编号
     */
    private Integer citySysNo;

    /**
     * 区名称
     */
    private String areaName;

    /**
     * 区编号
     */
    private Integer areaSysNo;

}
