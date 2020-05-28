package com.github.bluecatlee.common.third.kuaidi100.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LogisticsQueryResult implements Serializable {
    private String message;
    private String nu;
    private String ischeck;
    private String condition;
    private String com;
    private String status;
    private String state;
    private List<QueryDetail> data;

}
