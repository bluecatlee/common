package com.github.bluecatlee.common.excel.impl1.service.impl;

import com.github.bluecatlee.common.excel.impl1.AbstractPoiExcel;
import com.github.bluecatlee.common.excel.impl1.mapper.CommonReportMapper;
import com.github.bluecatlee.common.excel.impl1.service.CommonReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

//@Service("commonReportService")
public class CommonReportServiceImpl implements CommonReportService {

    @Autowired
    @SuppressWarnings("all")
    private CommonReportMapper commonReportMapper;

    @Override
    public Map<String,Object> commonReportQuery(Map<String, Object> params){
        // 返回总的【列名】【记录】的map集合(【列名】是第一条记录)
        Map<String, Object> returnMap = new HashMap<>();

        // 根据报表类别获取存储过程名称
        params.put("reportProcName", commonReportMapper.getReportProcNameByReportType(params));

        List<HashMap<String,Object>> resultMap = commonReportMapper.commonReportQuery(params);

        // 获取【输出参数】中的【总计】字符串
        if (StringUtils.isBlank((String)params.get("statisticsStr"))) {
            returnMap.put("statistics", null);
        } else {
            String statisticsStr = params.get("statisticsStr").toString();
            Map<String, Object> statisticsMap = new HashMap<>();
            String[] statisticsList = statisticsStr.split("####");
            for (String item : statisticsList) {
                statisticsMap.put(item.split("_")[0], item.split("_")[1]);
            }
            returnMap.put("statistics", statisticsMap);
        }

        // 列名集合
        List<Map<String, Object>> columnList = new ArrayList<>();
        // 获取【输出参数】中的【列名】字符串
        if (params.get("columnStr")==null || StringUtils.isBlank("columnStr")) {
            returnMap.put("column", null);
        } else {
            String columnStr = params.get("columnStr").toString();
            Map<String, Object> columnMap = new HashMap<>();
            String[] columnStrList = columnStr.split("####");
            for (String item : columnStrList) {
                Map<String, Object> mapColumn = new HashMap<>();
                mapColumn.put("prop", item.split("_")[1]);
                mapColumn.put("label", item.split("_")[2]);
                columnList.add(mapColumn);
            }
            returnMap.put("column", columnList);
        }

        // 获取数据
//        int index = 0;
        if(resultMap ==null || resultMap.size() == 0) {
            returnMap.put("list", Collections.emptyList());
        } else {
            // 返回总的【记录】集合
            List<Map<String, Object>> valueList = new ArrayList<>();
            for(HashMap<String, Object> bean : resultMap){
//                List<Map<String, Object>> columnList = new ArrayList<>();

                //这里将map.entrySet()转换成list
                List<Map.Entry<String,Object>> list = new ArrayList<Map.Entry<String,Object>>(bean.entrySet());
                //然后通过比较器来实现排序
                Collections.sort(list, new Comparator<Map.Entry<String,Object>>() {
                    //升序排序
                    public int compare(Map.Entry<String, Object> o1,
                                       Map.Entry<String, Object> o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });

                Map<String, Object> mapValue = new HashMap<>();
                for (Map.Entry<String, Object> entry : list) {
//                    if (index == 0) {
//                        Map<String, Object> mapColumn = new HashMap<>();
//                        mapColumn.put("prop", entry.getKey().split("_")[1]);
//                        mapColumn.put("label", entry.getKey().split("_")[2]);
//                        columnList.add(mapColumn);
//                    } else {
//                        mapValue.put(entry.getKey().split("_")[1], entry.getValue());
//                    }
                    mapValue.put(entry.getKey().split("_")[1], entry.getValue().toString());
                }
//                if (index == 0) {
//                    returnMap.put("column",columnList);
//                } else {
//                    valueList.add(mapValue);
//                }
                valueList.add(mapValue);

//                index++;
            }

            returnMap.put("list", valueList);

        }

        return returnMap;
    }

    @Override
    public void export(OutputStream os, Map<String,Object> params) {
        try {
            // 1：数据库查出数据
            Map<String,Object> returnMap = this.commonReportQuery(params);

            // 2：导出数据
            ReportExport poi = new ReportExport(returnMap);

            // 3：执行数据导出
            HSSFWorkbook excel = poi.export();

            // 4：返回数据流
            excel.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReportExport extends AbstractPoiExcel {

        private Map<String,Object> returnMap;

        public ReportExport(Map<String,Object> returnMap) {
            this.returnMap = returnMap;
        }

        @Override
        public void setData(HSSFWorkbook excel) {
            // 1: 整理数据，把数据拆分成各个面板数据

            // 2:导出第一个面板
            this.handleSheet(excel, 0, returnMap); // 第一个面板统计植发科信息

        }

        // 操作第一个面板
        private void handleSheet(HSSFWorkbook excel, int index, Map<String,Object> returnMap) {

            // 返回的列名
            List<Map<String, Object>> columnList = (List<Map<String, Object>>)returnMap.get("column");
            // 返回的记录
//            List<Map<String, Object>> resultList = (List<Map<String, Object>>)returnMap.get("list");
            List<Map<String, Object>> valueList = (List<Map<String, Object>>)returnMap.get("list");

            // 1：获取需要操作的面板
            HSSFSheet sheet = excel.getSheetAt(index);
            //设置列宽:第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2017-06-01"的宽度为2500
            for(int i=0; i<columnList.size(); i++){
                sheet.setColumnWidth(i, 4000);
            }
            //创建四周边框线
            HSSFCellStyle style = super.getRoundBorderStyle(excel);
            // 3：操作对应的单元格
            Integer rowIndex = 0;   //行索引值

            //总标题行
//            HSSFRow row = sheet.createRow(rowIndex++);
//            super.createStyleCell(row, 0, style).setCellValue("供货商供货明细表");

            //基本信息行
            HSSFRow row = null;

//            if(resultList!=null && resultList.size()>0){

                // 字段标题行
                row = sheet.createRow(rowIndex++);
                for(int i=0; i<columnList.size(); i++){
                    super.createStyleCell(row, i, style).setCellValue(columnList.get(i).get("label").toString());
                }

                // 【总计】行
                if (returnMap.get("statistics") != null) {
                    Map<String, Object> statisticsMap = (Map<String, Object>) returnMap.get("statistics");
                    for (Map.Entry<String, Object> entry : statisticsMap.entrySet()) {
                        row = sheet.createRow(rowIndex++);
                        super.createStyleCell(row, 0, style).setCellValue(entry.getKey() + "：");
                        super.createStyleCell(row, 1, style).setCellValue(entry.getValue().toString());
                    }
                }



                // 记录
//                for (int i=0; i<resultList.size(); i++) {
//
//                    List<Map<String, Object>> valueList = (List<Map<String, Object>>)resultList.get(i);
//
//                    //插入记录行
//                    row  = sheet.createRow(rowIndex++);
//
//                    for(int j=0; j<valueList.size(); j++){
//                        super.createStyleCell(row, j, style).setCellValue(valueList.get(j).get(columnList.get(j).get("prop").toString()).toString());
//                    }
//                }
                for (int i=0; i<valueList.size(); i++) {

                    //插入记录行
                    row  = sheet.createRow(rowIndex++);

                    for(int j=0; j<columnList.size(); j++){
//                        super.createStyleCell(row, i, style).setCellValue(columnList.get(i).get("label").toString());
                        super.createStyleCell(row, j, style).setCellValue(valueList.get(i).get(columnList.get(j).get("prop").toString()).toString());
                    }
                }

//            }
        }

        @Override
        public String getTemplate() {
            return "excel/commonReportExcelRecord.xls";
        }
    }

    @Override
    public String getReportName(Map<String, Object> params){
        return commonReportMapper.getReportNameByReportType(params);
    }

    @Override
    public void regionalSourcesExport(OutputStream os, List<List<Object>> ret) {
        try {
            // 1：数据库查出数据

            // 2：导出数据
            RegionalSourcesExport poi = new RegionalSourcesExport(ret);

            // 3：执行数据导出
            HSSFWorkbook excel = poi.export();

            // 4：返回数据流
            excel.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class RegionalSourcesExport extends AbstractPoiExcel {

        private List<List<Object>> ret;

        public RegionalSourcesExport(List<List<Object>> ret) {
            this.ret = ret;
        }

        @Override
        public void setData(HSSFWorkbook excel) {
            // 1: 整理数据，把数据拆分成各个面板数据

            // 2:导出第一个面板
            this.handleSheet(excel, 0, ret);

        }

        // 操作第一个面板
        private void handleSheet(HSSFWorkbook excel, int index, List<List<Object>> ret) {
            Map<String,Object> returnMap = new HashMap<>();
            // 返回的列名
            List<Map<String, Object>> columnList = (List<Map<String, Object>>) returnMap.get("column");
            // 返回的记录
            List<Map<String, Object>> valueList = (List<Map<String, Object>>) returnMap.get("list");

            // 1：获取需要操作的面板
            HSSFSheet sheet = excel.getSheetAt(index);
            //设置列宽:第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2017-06-01"的宽度为2500
            for (int i = 0; i < ret.size(); i++) {
                sheet.setColumnWidth(i, 4000);
            }
            //创建四周边框线
            HSSFCellStyle style = super.getRoundBorderStyle(excel);
            // 3：操作对应的单元格
            Integer rowIndex = 0;   //行索引值

            //基本信息行
            HSSFRow row = null;

            // 字段标题行
            row = sheet.createRow(rowIndex++);
            for (int i = 0; i < columnList.size(); i++) {
                super.createStyleCell(row, i, style).setCellValue(columnList.get(i).get("label").toString());
            }

            // 【总计】行
            if (returnMap.get("statistics") != null) {
                Map<String, Object> statisticsMap = (Map<String, Object>) returnMap.get("statistics");
                for (Map.Entry<String, Object> entry : statisticsMap.entrySet()) {
                    row = sheet.createRow(rowIndex++);
                    super.createStyleCell(row, 0, style).setCellValue(entry.getKey() + "：");
                    super.createStyleCell(row, 1, style).setCellValue(entry.getValue().toString());
                }
            }

            for (int i = 0; i < valueList.size(); i++) {

                //插入记录行
                row = sheet.createRow(rowIndex++);

                for (int j = 0; j < columnList.size(); j++) {
                    super.createStyleCell(row, j, style).setCellValue(valueList.get(i).get(columnList.get(j).get("prop").toString()).toString());
                }
            }
        }

        @Override
        public String getTemplate() {
            return "excel/commonReportExcelRecord.xls";
        }
    }
}