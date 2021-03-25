package com.github.bluecatlee.common.excel.impl2;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
@Service
public class BaseExcelUtil<T> {

    private static Logger logger = LoggerFactory.getLogger(BaseExcelUtil.class.getName());

    public final static String success = "Excel导出成功";

    public final static String Excel2003 = "2003";

    public final static String Excel2007 = "2007";

    private final static String FIREFOX = "Firefox";

    static {
        //防止Zip bomb detected
        //ZipSecureFile.setMinInflateRatio(0l);
    }
    
    /**
     * 构建模版/导出数据 <功能详细描述>
     * @param list 数据
     * @param titleName 表头名称
     * @param titleField 表头对应数据字段名 导出数据类型默认为字符串，field后加 '@'表示该列为数字
     * @param headerCheckList 表头对应的下拉列表数据
     * @param excelVersion excel版本 2007/2003
     * @param fileName 文件名称
     * @param response
     * @return
     */
    public static <T> Boolean buildModel(List<T> list, String[] titleName, String[] titleField, Map<String, String[]> headerCheckList, String fileName, String excelVersion, HttpServletResponse response) {
        ServletOutputStream os = null;
        try {
            Workbook wb = null;
            if (Excel2007.equals(excelVersion)) {
                fileName += ".xlsx";
                wb = writeXlsxData2007(list, titleName, titleField, headerCheckList, fileName);
            } else {
                fileName += ".xls";
                wb = writeXlsData2003(list, titleName, titleField);
            }
            if (wb == null) {
                return false;
            }
            response.setContentType("application/vnd.ms-excel");
            // 以保存或者直接打开的方式把Excel返回到页面
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            os = response.getOutputStream();
            try {
                wb.write(os);
            } catch (OpenXML4JRuntimeException e) {
                return false;
            }
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 读取文件中数据
     * @param is 输入流
     * @param cls bean类型
     * @param field 对应字段
     * @return
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("resource")
    public List<T> readFromXls(InputStream is, Class<T> cls, String[] field) throws IOException, InstantiationException, IllegalAccessException {
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
        is.close();
        List<T> list = new ArrayList<T>();
        // 循环工作表Sheet
        XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        if (hssfSheet == null) {
            return null;
        }
        // 循环行Row-从数据行开始
        for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            T t = cls.newInstance();
            XSSFRow hssfRow = hssfSheet.getRow(rowNum);
            HashMap<String, Object> map = new HashMap<String, Object>();
            // 循环row中的每一个单元格
            if (hssfRow == null) {
                continue;
            }
            for (int i = 0; i < hssfRow.getLastCellNum(); i++) {
                XSSFCell cell = hssfRow.getCell(i);
                // 格式转换
                String val = "";
                if (cell != null) {
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        val = cell.getStringCellValue();
                    } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                        val = cell.getBooleanCellValue() == true ? "true" : "false";
                    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        BigDecimal valtemplete = new BigDecimal(cell.getNumericCellValue() + "");
                        if (new BigDecimal(valtemplete.longValue()).compareTo(valtemplete) == 0) {
                            val = valtemplete.longValue() + "";
                        } else {
                            val = valtemplete.toString();
                        }

                    } else {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        val = cell.getStringCellValue();
                    }
                } else {
                    val = "";
                }
                for (int n = 0; n < field.length; n++) {
                    if (i == n && !field[n].contains("&")) {
                        map.put(field[n], cell == null ? "" : val);
                    } else if (i == n) {
                        map.put(field[n].split("&")[0], cell == null ? "" : val.split("-")[0]);
                    }
                }
            }
            transMap2Bean(map, t);
            list.add(t);

        }
        return list;
    }

    /**
     * 导出excel2003
     *      第一行是标题 第二行开始是数据
     * @param dataList      数据集合
     * @param titleName     标题名
     * @param tableField    字段名(与标题名对应)
     * @return
     */
    @SuppressWarnings("unused")
    private static <T> HSSFWorkbook writeXlsData2003(List<T> dataList, String[] titleName, String[] tableField) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (dataList != null) {
            for (T t : dataList) {
                Map<String, Object> map = new HashMap<String, Object>();
                // BeanUtils.populate(r, map);
                map = transBean2Map(t);
                list.add(map);
            }
        }
        List<Map<String, String>> header = new ArrayList<Map<String, String>>();
        for (int i = 0; i < titleName.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", titleName[i]);
            map.put("field", tableField[i]);
            header.add(map);
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = null;
        // 创建标题 以第一行作为标题
        row = sheet.createRow(0);
        HSSFCellStyle greenStyle = createGreenStyle(wb);
        if (header != null) {
            Cell cell = null;
            for (int i = 0; i < header.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(header.get(i).get("name") == null ? "" : header.get(i).get("name").toString());
                cell.setCellStyle(greenStyle);
                sheet.setColumnWidth(i, 4000);
            }
        } else if (list.size() > 0) {
            // 如果header不存在 则以字段名作为标题
            Cell cell = null;
            Object[] keys = list.get(0).keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(keys[i].toString());
            }
        }
        // 添加excel内容
        HSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Set<String> set = map.keySet();
            Object[] keys = set.toArray();
            row = sheet.createRow(i + 1);
            Cell cell = null;

            for (int j = 0; j < header.size(); j++) {
                cell = row.createCell(j);
                if (header != null) {
                    String value = "";
                    try {
                        value = map.get(header.get(j).get("field")).toString();
                    } catch (Exception e) {
                        value = "";
                        // e.printStackTrace();
                    }

                    cell.setCellValue(value);
                    cell.setCellStyle(style);
                } else {
                    cell.setCellValue(map.get(keys[j].toString()).toString());
                    cell.setCellStyle(style);
                }
            }
        }

        return wb;
    }

//    private static <T> HSSFWorkbook writeXlsData2003_3_14(List<T> dataList, String[] titleName, String[] tableField) {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (dataList != null) {
//            for (T t : dataList) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                // BeanUtils.populate(r, map);
//                map = transBean2Map(t);
//                list.add(map);
//            }
//        }
//        List<Map<String, String>> header = new ArrayList<Map<String, String>>();
//        for (int i = 0; i < titleName.length; i++) {
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("name", titleName[i]);
//            map.put("field", tableField[i]);
//            header.add(map);
//        }
//
//        HSSFWorkbook wb = new HSSFWorkbook();
//        HSSFSheet sheet = wb.createSheet();
//        HSSFRow row = null;
//        // 创建标题 以第一行作为标题
//        row = sheet.createRow(0);
//        HSSFCellStyle greenStyle = createGreenStyle(wb);
//        if (header != null) {
//            Cell cell = null;
//            for (int i = 0; i < header.size(); i++) {
//                cell = row.createCell(i);
//                cell.setCellValue(header.get(i).get("name") == null ? "" : header.get(i).get("name").toString());
//                cell.setCellStyle(greenStyle);
//                sheet.setColumnWidth(i, 4000);
//            }
//        } else if (list.size() > 0) {
//            // 如果header不存在 则以字段名作为标题
//            Cell cell = null;
//            Object[] keys = list.get(0).keySet().toArray();
//            for (int i = 0; i < keys.length; i++) {
//                cell = row.createCell(i);
//                cell.setCellValue(keys[i].toString());
//            }
//        }
//        // 添加excel内容
//        HSSFCellStyle style = sheet.getWorkbook().createCellStyle();
//        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//        for (int i = 0; i < list.size(); i++) {
//            Map<String, Object> map = list.get(i);
//            Set<String> set = map.keySet();
//            Object[] keys = set.toArray();
//            row = sheet.createRow(i + 1);
//            Cell cell = null;
//
//            for (int j = 0; j < header.size(); j++) {
//                cell = row.createCell(j);
//                if (header != null) {
//                    String value = "";
//                    try {
//                        value = map.get(header.get(j).get("field")).toString();
//                    } catch (Exception e) {
//                        value = "";
//                        // e.printStackTrace();
//                    }
//
//                    cell.setCellValue(value);
//                    cell.setCellStyle(style);
//                } else {
//                    cell.setCellValue(map.get(keys[j].toString()).toString());
//                    cell.setCellStyle(style);
//                }
//            }
//        }
//
//        return wb;
//    }

    /**
     * 导出2007
     * @param dataList 导出数据
     * @param titleName 文件头
     * @param titleField 对应bean属性
     * @param headerCheckList 下拉列表map<Feild,Value>
     * @param fileName 导出文件名
     * @return
     */
    @SuppressWarnings("unused")
    private static <T> SXSSFWorkbook writeXlsxData2007(List<T> dataList, String[] titleName, String[] titleField, Map<String, String[]> headerCheckList, String fileName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (titleName == null) {
            return null;
        }
        if (titleField == null) {
            return null;
        }
        if (titleName.length != titleField.length) {
            return null;
        }

        if (dataList != null) {
            for (T t : dataList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map = transBean2Map(t);
                list.add(map);
            }
        }
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
        SXSSFSheet sheet = wb.createSheet(fileName.split("\\.")[0]);
        XSSFCellStyle greenStyle = (XSSFCellStyle) createGreenStyle(wb);
        List<Map<String, String>> header = new ArrayList<Map<String, String>>();
        for (int i = 0; i < titleName.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", titleName[i]);
            map.put("field", titleField[i]);
            header.add(map);
            if (headerCheckList != null && headerCheckList.containsKey(titleField[i]) && headerCheckList.get(titleField[i]).length < 10) {
                // 添加验证
                DataValidation data_validation_list = setDataValidationList(sheet, headerCheckList.get(titleField[i]), 1, 1000000, i, i);
                // 设置提示内容,标题,内容
                data_validation_list.createPromptBox("提示", "请选择");
                data_validation_list.createErrorBox("错误", "请输入有效值");
                data_validation_list.setEmptyCellAllowed(false);
                data_validation_list.setShowErrorBox(true);
                data_validation_list.setShowPromptBox(true);
                // 工作表添加验证数据
                sheet.addValidationData(data_validation_list);
            } else if (headerCheckList != null && headerCheckList.containsKey(titleField[i])) {
                SXSSFSheet sheetName = wb.createSheet(titleName[i]);
                // 设置头
                SXSSFRow row = sheetName.createRow(0);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue("代码");
                cell1.setCellStyle(greenStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue("名称");
                cell2.setCellStyle(greenStyle);
                String[] nameList = headerCheckList.get(titleField[i]);
                for (int j = 0; j < nameList.length; j++) {
                    SXSSFRow rowJ = sheetName.createRow(j + 1);
                    Cell cellA = rowJ.createCell(0);
                    if (nameList[j].split("-").length < 1) {
                        continue;
                    }
                    cellA.setCellValue(nameList[j].split("-")[0]);
                    Cell cellB = rowJ.createCell(1);
                    cellB.setCellValue(nameList[j].split("-")[1]);
                }

            }
        }

        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        SXSSFRow row = null;
        // 创建表头
        row = sheet.createRow(0);
        if (header != null) {
            Cell cell = null;
            for (int i = 0; i < header.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(header.get(i).get("name") == null ? "" : header.get(i).get("name").toString());
                cell.setCellStyle(greenStyle);
//                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellType(CellType.STRING);
                sheet.setColumnWidth(i, 4000);
            }
        } else if (list.size() > 0) {
            Cell cell = null;
            Object[] keys = list.get(0).keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(keys[i].toString());
//                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellType(CellType.STRING);
            }
        }

        // 添加excel内容
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Set<String> set = map.keySet();
            Object[] keys = set.toArray();
            row = sheet.createRow(i + 1);
            row.setRowStyle(style);
            Cell cell = null;

            for (int j = 0; j < header.size(); j++) {
                if (header != null) {
                    String value = "";
                    try {
                        String key = header.get(j).get("field");
                        if (key.contains("@")) {
//                            cell = row.createCell(j, Cell.CELL_TYPE_NUMERIC);
                            cell = row.createCell(j, CellType.NUMERIC);
                            value = map.get(key.replace("@", "")).toString();
//                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellValue(Double.valueOf(value));
                            cell.setCellStyle(style);
                        } else {
//                            cell = row.createCell(j, Cell.CELL_TYPE_STRING);
                            cell = row.createCell(j, CellType.STRING);
                            value = map.get(key).toString();
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                        }
                    } catch (Exception e) {
                        value = "";
                        // e.printStackTrace();
                    }

                } else {
                    cell.setCellValue(map.get(keys[j].toString()).toString());
                    cell.setCellStyle(style);
                }
            }
        }

        return wb;
    }

//    private static <T> SXSSFWorkbook writeXlsxData2007_3_14(List<T> dataList, String[] titleName, String[] titleField, Map<String, String[]> headerCheckList, String fileName) {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (titleName == null) {
//            return null;
//        }
//        if (titleField == null) {
//            return null;
//        }
//        if (titleName.length != titleField.length) {
//            return null;
//        }
//
//        if (dataList != null) {
//            for (T t : dataList) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map = transBean2Map(t);
//                list.add(map);
//            }
//        }
//        SXSSFWorkbook wb = new SXSSFWorkbook(500);
//        SXSSFSheet sheet = wb.createSheet(fileName.split("\\.")[0]);
//        XSSFCellStyle greenStyle = (XSSFCellStyle) createGreenStyle(wb);
//        List<Map<String, String>> header = new ArrayList<Map<String, String>>();
//        for (int i = 0; i < titleName.length; i++) {
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("name", titleName[i]);
//            map.put("field", titleField[i]);
//            header.add(map);
//            if (headerCheckList != null && headerCheckList.containsKey(titleField[i]) && headerCheckList.get(titleField[i]).length < 10) {
//                // 添加验证
//                DataValidation data_validation_list = setDataValidationList(sheet, headerCheckList.get(titleField[i]), 1, 1000000, i, i);
//                // 设置提示内容,标题,内容
//                data_validation_list.createPromptBox("提示", "请选择");
//                data_validation_list.createErrorBox("错误", "请输入有效值");
//                data_validation_list.setEmptyCellAllowed(false);
//                data_validation_list.setShowErrorBox(true);
//                data_validation_list.setShowPromptBox(true);
//                // 工作表添加验证数据
//                sheet.addValidationData(data_validation_list);
//            } else if (headerCheckList != null && headerCheckList.containsKey(titleField[i])) {
//                SXSSFSheet sheetName = wb.createSheet(titleName[i]);
//                // 设置头
//                SXSSFRow row = sheetName.createRow(0);
//                Cell cell1 = row.createCell(0);
//                cell1.setCellValue("代码");
//                cell1.setCellStyle(greenStyle);
//                Cell cell2 = row.createCell(1);
//                cell2.setCellValue("名称");
//                cell2.setCellStyle(greenStyle);
//                String[] nameList = headerCheckList.get(titleField[i]);
//                for (int j = 0; j < nameList.length; j++) {
//                    SXSSFRow rowJ = sheetName.createRow(j + 1);
//                    Cell cellA = rowJ.createCell(0);
//                    if (nameList[j].split("-").length < 1) {
//                        continue;
//                    }
//                    cellA.setCellValue(nameList[j].split("-")[0]);
//                    Cell cellB = rowJ.createCell(1);
//                    cellB.setCellValue(nameList[j].split("-")[1]);
//                }
//
//            }
//        }
//
//        CellStyle style = sheet.getWorkbook().createCellStyle();
//        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//        SXSSFRow row = null;
//        // 创建表头
//        row = sheet.createRow(0);
//        if (header != null) {
//            Cell cell = null;
//            for (int i = 0; i < header.size(); i++) {
//                cell = row.createCell(i);
//                cell.setCellValue(header.get(i).get("name") == null ? "" : header.get(i).get("name").toString());
//                cell.setCellStyle(greenStyle);
//                cell.setCellType(Cell.CELL_TYPE_STRING);
//                sheet.setColumnWidth(i, 4000);
//            }
//        } else if (list.size() > 0) {
//            Cell cell = null;
//            Object[] keys = list.get(0).keySet().toArray();
//            for (int i = 0; i < keys.length; i++) {
//                cell = row.createCell(i);
//                cell.setCellValue(keys[i].toString());
//                cell.setCellType(Cell.CELL_TYPE_STRING);
//            }
//        }
//
//        // 添加excel内容
//        for (int i = 0; i < list.size(); i++) {
//            Map<String, Object> map = list.get(i);
//            Set<String> set = map.keySet();
//            Object[] keys = set.toArray();
//            row = sheet.createRow(i + 1);
//            row.setRowStyle(style);
//            Cell cell = null;
//
//            for (int j = 0; j < header.size(); j++) {
//                if (header != null) {
//                    String value = "";
//                    try {
//                        String key = header.get(j).get("field");
//                        if (key.contains("@")) {
//                            cell = row.createCell(j, Cell.CELL_TYPE_NUMERIC);
//                            value = map.get(key.replace("@", "")).toString();
//                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
//                            cell.setCellValue(Double.valueOf(value));
//                            cell.setCellStyle(style);
//                        } else {
//                            cell = row.createCell(j, Cell.CELL_TYPE_STRING);
//                            value = map.get(key).toString();
//                            cell.setCellValue(value);
//                            cell.setCellStyle(style);
//                        }
//                    } catch (Exception e) {
//                        value = "";
//                        // e.printStackTrace();
//                    }
//
//                } else {
//                    cell.setCellValue(map.get(keys[j].toString()).toString());
//                    cell.setCellStyle(style);
//                }
//            }
//        }
//
//        return wb;
//    }

    /**
     * 保存数据到excel文件
     * @param list 数据
     * @param filePath 导保存文件目录
     * @param out 文件输出流
     * @param result 返回结果
     * @param header excel头
     * @return
     */
    @SuppressWarnings("unused")
    private Map<String, Object> writeXlsxData(List<Map<String, Object>> list, String filePath, FileOutputStream out, Map<String, Object> result, ArrayList<String> header) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();

        XSSFRow row = null;
        // 添加excel头
        row = sheet.createRow(0);
        if (header != null && header.size() >= list.get(0).keySet().size()) {
            Cell cell = null;
            for (int i = 0; i < header.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(header.get(i));
            }
        } else {
            Cell cell = null;
            Object[] keys = list.get(0).keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(keys[i].toString());
            }
        }
        // 添加excel内容
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Set<String> set = map.keySet();
            Object[] keys = set.toArray();
            row = sheet.createRow(i + 1);
            Cell cell = null;
            for (int j = 0; j < keys.length; j++) {
                cell = row.createCell(j);
                if (header != null && header.size() >= list.get(0).keySet().size()) {
                    cell.setCellValue(map.get(header.get(j)).toString());
                } else {
                    cell.setCellValue(map.get(keys[j].toString()).toString());
                }
            }
        }

        try {
            wb.write(out);
            wb.close();
            out.flush();
            out.close();

            result.put("code", 1);
            result.put("message", "成功导出" + list.size() + "条记录到" + filePath);
        } catch (Exception e) {
            result.put("code", -200);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 保存数据到excel文件
     * @param list 数据
     * @param filePath 导保存文件目录
     * @param out 文件输出流
     * @param result 返回结果
     * @param header excel头
     * @return
     */
    @SuppressWarnings("unused")
    private Map<String, Object> writeXlsData(List<Map<String, Object>> list, String filePath, FileOutputStream out, Map<String, Object> result, ArrayList<String> header) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        HSSFRow row = null;
        // 添加excel头
        row = sheet.createRow(0);
        if (header != null && header.size() >= list.get(0).keySet().size()) {
            Cell cell = null;
            for (int i = 0; i < header.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(header.get(i));
            }
        } else {
            Cell cell = null;
            Object[] keys = list.get(0).keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(keys[i].toString());
            }
        }
        // 添加excel内容
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Set<String> set = map.keySet();
            Object[] keys = set.toArray();
            row = sheet.createRow(i + 1);
            Cell cell = null;
            for (int j = 0; j < keys.length; j++) {
                cell = row.createCell(j);
                if (header != null && header.size() >= list.get(0).keySet().size()) {
                    cell.setCellValue(map.get(header.get(j)).toString());
                } else {
                    cell.setCellValue(map.get(keys[j].toString()).toString());
                }
            }
        }

        try {
            wb.write(out);
            wb.close();
            out.flush();
            out.close();

            result.put("code", 1);
            result.put("message", "成功导出" + list.size() + "条记录到" + filePath);
        } catch (Exception e) {
            result.put("code", -200);
            result.put("message", e.getMessage());
        }
        return result;
    }

    private static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if (value == null) {
                        continue;
                    }
                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }

    /**
     * 设置样式
     * @param wb
     * @return
     */
    private static CellStyle createGreenStyle(SXSSFWorkbook wb) {
        // 设置字体
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName("宋体"); // 字体
        font.setBold(true);

        CellStyle greenStyle = wb.createCellStyle();
        greenStyle.setFillBackgroundColor(FillPatternType.LEAST_DOTS.getCode()); //?
        greenStyle.setFillPattern(FillPatternType.LEAST_DOTS);
        greenStyle.setAlignment(HorizontalAlignment.CENTER);
        greenStyle.setBorderBottom(BorderStyle.MEDIUM);
        greenStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderLeft(BorderStyle.MEDIUM);
        greenStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderRight(BorderStyle.MEDIUM);
        greenStyle.setRightBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderTop(BorderStyle.MEDIUM);
        greenStyle.setTopBorderColor(IndexedColors.BLACK.index);
        greenStyle.setFont(font);
        greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        greenStyle.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
        greenStyle.setWrapText(true);

        return greenStyle;
    }

//    private static CellStyle createGreenStyle_3_14(SXSSFWorkbook wb) {
//        // 设置字体
//        Font font = wb.createFont();
//        font.setFontHeightInPoints((short) 11); // 字体高度
//        font.setFontName("宋体"); // 字体
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
//
//        CellStyle greenStyle = wb.createCellStyle();
//        greenStyle.setFillBackgroundColor(HSSFCellStyle.LEAST_DOTS);
//        greenStyle.setFillPattern(HSSFCellStyle.LEAST_DOTS);
//        greenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
//        greenStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setBottomBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setLeftBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setRightBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setTopBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setFont(font);
//        greenStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
//        greenStyle.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
//        greenStyle.setWrapText(true);
//
//        return greenStyle;
//    }

    /**
     * 设置Excel样式2003
     * @param wb
     * @return
     */
    private static HSSFCellStyle createGreenStyle(HSSFWorkbook wb) {
        // 设置字体
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName("宋体"); // 字体
        font.setBold(true);

        HSSFCellStyle greenStyle = wb.createCellStyle();
        greenStyle.setFillBackgroundColor(FillPatternType.LEAST_DOTS.getCode()); //?
        greenStyle.setFillPattern(FillPatternType.LEAST_DOTS);
        greenStyle.setAlignment(HorizontalAlignment.CENTER);
        greenStyle.setBorderBottom(BorderStyle.MEDIUM);
        greenStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderLeft(BorderStyle.MEDIUM);
        greenStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderRight(BorderStyle.MEDIUM);
        greenStyle.setRightBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderTop(BorderStyle.MEDIUM);
        greenStyle.setTopBorderColor(IndexedColors.BLACK.index);
        greenStyle.setFont(font);
        greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        greenStyle.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
        greenStyle.setWrapText(true);

        return greenStyle;
    }

//    private static HSSFCellStyle createGreenStyle_3_14(HSSFWorkbook wb) {
//        // 设置字体
//        Font font = wb.createFont();
//        font.setFontHeightInPoints((short) 11); // 字体高度
//        font.setFontName("宋体"); // 字体
//        font.setBold(true);
//
//        HSSFCellStyle greenStyle = wb.createCellStyle();
//        greenStyle.setFillBackgroundColor(HSSFCellStyle.LEAST_DOTS);
//        greenStyle.setFillPattern(HSSFCellStyle.LEAST_DOTS);
//        greenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
//        greenStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setBottomBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setLeftBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setRightBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setTopBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setFont(font);
//        greenStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
//        greenStyle.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
//        greenStyle.setWrapText(true);
//
//        return greenStyle;
//    }

    /**
     * 设置Excel样式2007
     * @param wb
     * @return
     */
    @SuppressWarnings("unused")
    private static XSSFCellStyle createGreenStyle(XSSFWorkbook wb) {
        // 设置字体
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName("宋体"); // 字体
        font.setBold(true);

        XSSFCellStyle greenStyle = wb.createCellStyle();
        greenStyle.setFillBackgroundColor(FillPatternType.LEAST_DOTS.getCode()); //?
        greenStyle.setFillPattern(FillPatternType.LEAST_DOTS);
        greenStyle.setAlignment(HorizontalAlignment.CENTER);
        greenStyle.setBorderBottom(BorderStyle.MEDIUM);
        greenStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderLeft(BorderStyle.MEDIUM);
        greenStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderRight(BorderStyle.MEDIUM);
        greenStyle.setRightBorderColor(IndexedColors.BLACK.index);
        greenStyle.setBorderTop(BorderStyle.MEDIUM);
        greenStyle.setTopBorderColor(IndexedColors.BLACK.index);
        greenStyle.setFont(font);
        greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        greenStyle.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
        greenStyle.setWrapText(true);

        return greenStyle;
    }

//    private static XSSFCellStyle createGreenStyle_3_14(XSSFWorkbook wb) {
//        // 设置字体
//        XSSFFont font = wb.createFont();
//        font.setFontHeightInPoints((short) 11); // 字体高度
//        font.setFontName("宋体"); // 字体
//        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
//
//        XSSFCellStyle greenStyle = wb.createCellStyle();
//        greenStyle.setFillBackgroundColor(XSSFCellStyle.LEAST_DOTS);
//        greenStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
//        greenStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
//        greenStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setBottomBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setLeftBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setRightBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
//        greenStyle.setTopBorderColor(HSSFColor.BLACK.index);
//        greenStyle.setFont(font);
//        greenStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
//        greenStyle.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
//        greenStyle.setWrapText(true);
//
//        return greenStyle;
//    }

    /**
     * 设置excel数据有效性
     * @param sheet
     * @param firstRow 起始行
     * @param firstCol 终止行
     * @param endRow 起始列
     * @param endCol 终止列
     * @return
     */
    private static DataValidation setDataValidationList(SXSSFSheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textlist);
        constraint.setExplicitListValues(textlist);

        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);

        DataValidation data_validation = helper.createValidation(constraint, regions);

        return data_validation;
    }

    /**
     * map转对象
     * @param map
     * @param obj
     */
    private static void transMap2Bean(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法

                    Method setter = property.getWriteMethod();
                    String type = property.getPropertyType().toString();

                    if (type.contains("String")) {
                        setter.invoke(obj, value.toString());
                    } else if (type.contains("BigDecimal")) {
                        setter.invoke(obj, new BigDecimal(value.toString()));
                    } else if (type.contains("Integer")) {
                        setter.invoke(obj, (int) Double.parseDouble(value.toString()));
                    } else if (type.contains("long") || type.contains("Long")) {
                        BigDecimal bd;
                        try {
                            bd = new BigDecimal(value.toString());
                            setter.invoke(obj, Long.parseLong(bd.toPlainString()));
                        } catch (Exception e) {
                            setter.invoke(obj, Long.MIN_VALUE);
                        }

                    } else if (type.contains("int")) {
                        if (value instanceof String) {
                            try {
                                String val = ((String) value).split("\\.")[0];
                                setter.invoke(obj, Integer.parseInt(val));
                            } catch (Exception e) {
                                setter.invoke(obj, Long.MIN_VALUE);
                            }
                        }
                    }else if(type.contains("Date")){
                       if(value == null){
                           setter.invoke(obj,null);
                       }else if(value.getClass().toString().contains("String")){
                           setter.invoke(obj, null);
                       }else{
                           try {
                               Date date = (Date) value;
                               SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                   "yyyy-MM-dd hh:mm:ss");
                               String format = simpleDateFormat.format(value);
                               setter.invoke(obj, format);
                           }catch (Exception e){
                               logger.info("excel parse error ");
                           }
                       }
                    } else {
                        setter.invoke(obj, value);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    /**
     * 数据转ArrayList
     * @param strs
     * @return
     */
    @SuppressWarnings("unused")
    private ArrayList<String> convertStrs2ArrayList(String[] strs) {
        if (strs == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(strs));
        return list;
    }

    /**
     * 构建excel模板
     * @param tableName 表头名
     * @param sheetName excel文件名
     * @return
     */
    public static SXSSFSheet templateBuild(String[] tableName, String sheetName) {
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        SXSSFSheet sheet = wb.createSheet(sheetName);
        templateBuild(sheet, tableName, 0);
        return sheet;
    }

    /**
     * 构建excel模板
     * @param sheet
     * @param tableName 表头名
     * @param rowIndex
     * @return
     */
    public static void templateBuild(SXSSFSheet sheet, String[] tableName, int rowIndex) {
        XSSFCellStyle greenStyle = (XSSFCellStyle) createGreenStyle(sheet.getWorkbook());

        // 添加excel头
        SXSSFRow row = sheet.createRow(rowIndex);

        if (!ArrayUtils.isEmpty(tableName)) {
            Cell cell;
            for (int i = 0; i < tableName.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(tableName[i] == null ? "" : tableName[i]);
                cell.setCellStyle(greenStyle);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                sheet.setColumnWidth(i, 4000);
            }
        }
    }

    public static CellStyle createGeneralCellStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

//    public static CellStyle createGeneralCellStyle_3_14(SXSSFWorkbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//        return style;
//    }

    public static CellStyle createCellStyle(SXSSFWorkbook workbook, short hssfDataFormat) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(hssfDataFormat);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

//    public static CellStyle createCellStyle(SXSSFWorkbook workbook, short hssfDataFormat) {
//        CellStyle style = workbook.createCellStyle();
//        style.setDataFormat(hssfDataFormat);
//        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//        return style;
//    }

    public static void cellBuild(int rowIndex, int colIndex, SXSSFSheet sheet, CellStyle style, int cellType, Object cellValue) {
        SXSSFRow row = sheet.getRow(rowIndex);
        if (null == row) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.createCell(colIndex);
        cell.setCellStyle(style);
        if (null == cellValue) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return;
        } else {
            cell.setCellType(cellType);
        }
        if (Objects.equals(cellType, Cell.CELL_TYPE_NUMERIC) && cellValue instanceof Double) {
            cell.setCellValue(Double.valueOf(cellValue.toString()));
        } else if (Objects.equals(cellType, Cell.CELL_TYPE_NUMERIC) && (cellValue instanceof Integer)) {
            cell.setCellValue(Integer.valueOf(cellValue.toString()));
        } else if (Objects.equals(cellType, Cell.CELL_TYPE_NUMERIC) && (cellValue instanceof Long)) {
            cell.setCellValue(Long.valueOf(cellValue.toString()));
        } else {
            cell.setCellValue(cellValue.toString());
        }

    }

    public static SXSSFWorkbook sheetBuild(String[] tableName, String sheetName, SXSSFWorkbook wb) {
        SXSSFSheet sheet = wb.createSheet(sheetName);
        XSSFCellStyle greenStyle = (XSSFCellStyle) createGreenStyle(wb);

        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        SXSSFRow row = null;
        // 添加excel头
        row = sheet.createRow(0);

        if (!ArrayUtils.isEmpty(tableName)) {
            Cell cell = null;
            for (int i = 0; i < tableName.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(tableName[i] == null ? "" : tableName[i]);
                cell.setCellStyle(greenStyle);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                sheet.setColumnWidth(i, 4000);
            }
        }
        return wb;
    }

//    public static SXSSFWorkbook sheetBuild_3_14(String[] tableName, String sheetName, SXSSFWorkbook wb) {
//        SXSSFSheet sheet = wb.createSheet(sheetName);
//        XSSFCellStyle greenStyle = (XSSFCellStyle) createGreenStyle(wb);
//
//        CellStyle style = sheet.getWorkbook().createCellStyle();
//        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//        SXSSFRow row = null;
//        // 添加excel头
//        row = sheet.createRow(0);
//
//        if (!ArrayUtils.isEmpty(tableName)) {
//            Cell cell = null;
//            for (int i = 0; i < tableName.length; i++) {
//                cell = row.createCell(i);
//                cell.setCellValue(tableName[i] == null ? "" : tableName[i]);
//                cell.setCellStyle(greenStyle);
//                cell.setCellType(Cell.CELL_TYPE_STRING);
//                sheet.setColumnWidth(i, 4000);
//            }
//        }
//        return wb;
//    }

    /**
     * 在已存在的excel上追加数据
     * @param data
     * @param tableField
     * @param wb
     * @return
     */
    public SXSSFWorkbook addToBuild(List<T> data, String[] tableField, SXSSFWorkbook wb) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (null != data && !data.isEmpty()) {
            for (T t : data) {
                Map<String, Object> map = new HashMap<String, Object>();
                map = this.transBean2Map(t);
                list.add(map);
            }
        }
        SXSSFSheet sheet = wb.getSheetAt(0);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        int lastRowNum = sheet.getLastRowNum()+1;
        // 添加excel内容
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            SXSSFRow row=sheet.createRow(i + lastRowNum);
            row.setRowStyle(style);
            Cell cell = null;

            for (int j = 0; j < tableField.length; j++) {
                String value = "";
                try {
                    String key = tableField[j];
                    if (key.contains("@")) {
                        cell = row.createCell(j, Cell.CELL_TYPE_NUMERIC);
                        value = map.get(key.replace("@", "")).toString();
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(Double.valueOf(value));
                        cell.setCellStyle(style);
                    } else {
                        cell = row.createCell(j, Cell.CELL_TYPE_STRING);
                        value = map.get(key).toString();
                        cell.setCellValue(value);
                        cell.setCellStyle(style);
                    }
                } catch (Exception e) {
                    value = "";
                }
            }
        }
        return wb;
    }

//    public SXSSFWorkbook addToBuild_3_14(List<T> data, String[] tableField, SXSSFWorkbook wb) {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//        if (null != data && !data.isEmpty()) {
//            for (T t : data) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map = this.transBean2Map(t);
//                list.add(map);
//            }
//        }
//        SXSSFSheet sheet = wb.getSheetAt(0);
//        CellStyle style = sheet.getWorkbook().createCellStyle();
//        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//        int lastRowNum = sheet.getLastRowNum()+1;
//        // 添加excel内容
//        for (int i = 0; i < list.size(); i++) {
//            Map<String, Object> map = list.get(i);
//            SXSSFRow row=sheet.createRow(i + lastRowNum);
//            row.setRowStyle(style);
//            Cell cell = null;
//
//            for (int j = 0; j < tableField.length; j++) {
//                String value = "";
//                try {
//                    String key = tableField[j];
//                    if (key.contains("@")) {
//                        cell = row.createCell(j, Cell.CELL_TYPE_NUMERIC);
//                        value = map.get(key.replace("@", "")).toString();
//                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
//                        cell.setCellValue(Double.valueOf(value));
//                        cell.setCellStyle(style);
//                    } else {
//                        cell = row.createCell(j, Cell.CELL_TYPE_STRING);
//                        value = map.get(key).toString();
//                        cell.setCellValue(value);
//                        cell.setCellStyle(style);
//                    }
//                } catch (Exception e) {
//                    value = "";
//                }
//            }
//        }
//        return wb;
//    }

    public static void downloadDataModel(HttpServletResponse res, SXSSFWorkbook wb, String fileName) {
        downloadDataModel(res, wb, fileName, null);
    }

    public static void downloadDataModel(HttpServletResponse res, SXSSFWorkbook wb, String fileName, HttpServletRequest req) {
        if (req == null) {

        }

        if (wb == null) {
            return;
        }
        ServletOutputStream os;
        try {
            res.setContentType("application/vnd.ms-excel");
            // 以保存或者直接打开的方式把Excel返回到页面
            if( isChrome(req) || req == null){
                res.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName+".xlsx", "UTF-8"));
            }else
            if (regexBrowser(FIREFOX, req) || req.getHeader("user-agent").toLowerCase().contains("safari")) {
                // todo fix  req safari 有问题, 因为 Safari 存在在chrome ua 里.
                res.setHeader("Content-Disposition", "attachment; filename=" + new String((fileName + ".xlsx").getBytes("GB2312"),"ISO-8859-1"));
            } else {
                res.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName+".xlsx", "UTF-8"));
            }

            os = res.getOutputStream();

            wb.write(os);
            os.flush();
            os.close();
            logger.info("excel导出成功");
        } catch (Exception e) {
            logger.error("导出excel发生异常", e);
        }

    }

    private static boolean isChrome(HttpServletRequest req) {
      if(req == null ){
          return false;
      }
        String ua = req.getHeader("User-Agent");
         if(ua == null){
             ua = req.getHeader("User-agent");
         }
         if(ua == null){
             ua = req.getHeader("user-agent");
         }
         if(ua == null){
             return false;
         }

         return regex( "Chrome" , ua);
    }

    private static boolean regexBrowser(String regex, HttpServletRequest req){
        if (req == null) {
            return false;
        }
        String str = req.getHeader("user-agent");
        if(str == null){
            return false;
        }
        return regex(regex, str);
    }

    private static boolean regex(String regex, String str){
        Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 进行sheet写操作的sheet。
     */
    protected static class PoiWriter implements Runnable {

        private final CountDownLatch doneSignal;

        private SXSSFSheet sheet;

        private List<Map<String, Object>> list;

        private int lastRowNum;

        private CellStyle style;

        private String[] tableField;

        public PoiWriter(CountDownLatch doneSignal, SXSSFSheet sheet, int lastRowNum, List<Map<String, Object>> list, CellStyle style, String[] tableField) {
            this.doneSignal = doneSignal;
            this.sheet = sheet;
            this.lastRowNum = lastRowNum;
            this.list = list;
            this.style = style;
            this.tableField = tableField;
        }

        public void run() {
            try {
                if(null != list && !list.isEmpty()){
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> map = list.get(i);
                        //SXSSFRow row=sheet.createRow(i + lastRowNum);
                        SXSSFRow row=getRow(sheet,i + lastRowNum);
                        row.setRowStyle(style);
                        Cell cell = null;
                        for (int j = 0; j < tableField.length; j++) {
                            String value = "";
                            try {
                                String key = tableField[j];
                                if (key.contains("@")) {
                                    cell = row.createCell(j, Cell.CELL_TYPE_NUMERIC);
                                    value = map.get(key.replace("@", "")).toString();
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue(Double.valueOf(value));
                                    cell.setCellStyle(style);
                                } else {
                                    cell = row.createCell(j, Cell.CELL_TYPE_STRING);
                                    value = map.get(key).toString();
                                    cell.setCellValue(value);
                                    cell.setCellStyle(style);
                                }
                            } catch (Exception e) {
                                value = "";
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                doneSignal.countDown();
            }
        }

    }

    private static synchronized SXSSFRow getRow(SXSSFSheet sheet, int rownum) {
        return sheet.createRow(rownum);
    }

    public static void setSheetColumnWidth(Sheet sheet, int[] columnIndex, int[] widthIndex) {
        int minIndex = columnIndex.length > widthIndex.length ? widthIndex.length : columnIndex.length;
        for(int i = 0; i < minIndex; i++){
            sheet.setColumnWidth(columnIndex[i], widthIndex[i]);
        }
    }

    public static XSSFWorkbook readFileToWorkbook(InputStream inputStream) {
        XSSFWorkbook book=null;

        try{
            book= new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            return null;
        }
        return book;
    }

    /**
     * 读取文件中数据
     *      主要修改是增加了 Date类型的转换, 会利用poi内置的日期转换功能.
     * @param cls bean类型
     * @param field 对应字段
     */
    @SuppressWarnings("resource")
    static public <T> List<T> readExcelAsListFromXls(XSSFWorkbook hssfWorkbook, Class<T> cls, String[] field) throws IOException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<T>();
        // 循环工作表Sheet
        XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        if (hssfSheet == null) {
            return null;
        }
        // 循环行Row-从数据行开始
        for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            T t = cls.newInstance();
            XSSFRow hssfRow = hssfSheet.getRow(rowNum);
            HashMap<String, Object> map = new HashMap<String, Object>();
            // 循环row中的每一个单元格
            if (hssfRow == null) {
                continue;
            }
            for (int i = 0; i < hssfRow.getLastCellNum(); i++) {
                XSSFCell cell = hssfRow.getCell(i);
                // 格式转换
                Object val = "";
                if (cell != null) {
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        val = cell.getStringCellValue();
                    } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                        val = cell.getBooleanCellValue() == true ? "true" : "false";
                    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        //数字格式, 包含日期.进一步处理
                        XSSFCellStyle cellStyle = cell.getCellStyle();
                        short dataFormat = cellStyle.getDataFormat();
                        if(dataFormat<= 22 && dataFormat>=18 || dataFormat == 14){
                            Date value = cell.getDateCellValue();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                                               "yyyy-MM-dd hh:mm:ss");
                            String format = simpleDateFormat.format(value);
                            val = format;
                        }else {
                            BigDecimal valtemplete = new BigDecimal(cell.getNumericCellValue() + "");
                            if (new BigDecimal(valtemplete.longValue()).compareTo(valtemplete) == 0) {
                                val = valtemplete.longValue() + "";
                            } else {
                                val = valtemplete.toString();
                            }
                        }

                    } else {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        val = cell.getStringCellValue();
                    }
                } else {
                    val = "";
                }
                if (i >= field.length) {
                    //do sth, 这条数据已经超出需要读入的field数目限制了.
                    continue;
                }
                String s = field[i];
                if (!s.contains("&")) {
                    map.put(s, cell == null ? "" : val);
                } else {
                    map.put(s.split("&")[0], cell == null ? "" : val);
                }
            }
            transMap2Bean(map, t);

            list.add(t);

        }
        return list;
    }
    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名  
        String fileName = file.getOriginalFilename();  
        //创建Workbook工作薄对象，表示整个excel  
        Workbook workbook = null;
        try {  
            //获取excel文件的io流  
            InputStream is = file.getInputStream();  
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象  
            if(fileName.endsWith("xls")){  
                //2003  
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith("xlsx")){  
                //2007  
                workbook = new XSSFWorkbook(is);
            }  
        } catch (IOException e) {  
            logger.info(e.getMessage());  
        }  
        return workbook;  
    }

    public static String getCellValue(Cell cell){
        String cellValue = "";  
        if(cell == null){  
            return cellValue;  
        }  
        //把数字当成String来读，避免出现1读成1.0的情况  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }  
        //判断数据的类型  
        switch (cell.getCellType()){  
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());  
                break;  
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";  
                break;  
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";  
                break;  
            default:  
                cellValue = "未知类型";  
                break;  
        }  
        return cellValue;  
    }

}
