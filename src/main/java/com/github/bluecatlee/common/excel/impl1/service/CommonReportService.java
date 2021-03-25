package com.github.bluecatlee.common.excel.impl1.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface CommonReportService {

    /**
     * 通用报表查询
     */
    Map<String,Object> commonReportQuery(Map<String, Object> params);

    /**
     * 获取报表名称
     */
    String getReportName(Map<String, Object> params);

    /**
     * 通用报表导出
     */
    void export(OutputStream os, Map<String, Object> params);

    void regionalSourcesExport(OutputStream os, List<List<Object>> ret);

}