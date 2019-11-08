package com.github.bluecatlee.common.excel.frame.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CommonReportMapper {

    /**
     * 通用报表查询
     * @param params
     * @return
     */
    List<HashMap<String,Object>> commonReportQuery(Map<String, Object> params);

    /**
     * 根据报表类别获取报表名称
     * @param params
     * @return
     */
    String getReportNameByReportType(Map<String, Object> params);

    /**
     * 根据报表类别获取存储过程名称
     * @param params
     * @return
     */
    String getReportProcNameByReportType(Map<String, Object> params);

}