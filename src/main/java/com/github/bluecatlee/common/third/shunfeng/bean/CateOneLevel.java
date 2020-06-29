package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

import java.util.List;

@Data
public class CateOneLevel {

    /**
     * 类别编号
     */
    private Integer categoryNo;

    /**
     *  类别名称
     */
    private String categoryName;

    /**
     * 子分类
     */
    private List<CateTwoLevel> operCateTwoLeves;

}
