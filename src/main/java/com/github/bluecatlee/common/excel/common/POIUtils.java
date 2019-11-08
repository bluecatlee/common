// package com.github.bluecatlee.common.excel.common;
//
// import org.apache.poi.hssf.usermodel.*;
// import org.apache.poi.poifs.filesystem.POIFSFileSystem;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.multipart.MultipartHttpServletRequest;
//
// import javax.servlet.http.HttpServletResponse;
// import java.io.InputStream;
// import java.io.OutputStream;
// import java.io.UnsupportedEncodingException;
// import java.util.List;
//
// public class POIUtils {
//
//     public void exportExcel(HttpServletResponse response) {
//         HSSFWorkbook wb = new HSSFWorkbook();                  //创建工作簿workbook
//         HSSFSheet sheet = wb.createSheet("new Sheet");         //创建工作表sheet
//
//         sheet.createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow);
//         //创建冻结窗口 前两个参数是要用来拆分的列数和行数、后两个参数是下面窗口的可见象限，其中第三个参数是右边区域可见的左边列数，第四个参数是下面区域可见的首行。
//         //sheet.createFreezePane( 0, 1, 0, 1 );                //冻结第一行
//         //sheet.createFreezePane( 1, 0, 1, 0 );                //冻结第一列
//         sheet.setColumnWidth(int columnIndex, int width);      //设置列宽
//
//         HSSFRow row = sheet.createRow(int rownum);             //创建单元行 0表示第一行
//         HSSFCell cell = row.createCell(int column);		       //创建单元格 0表示第一列，也就是第一个单元格
//
//         HSSFCellStyle style = wb.createCellStyle();            //创建单元格样式
//         style.setAlignment(short align);                       //设置样式的对齐方式，参数是HSSFCellStyle接口的静态常量，如HSSFCellStyle.ALIGN_CENTER表示居中
//
//         cell.setCellValue(String value);                       //设置单元格内容
//         cell.setCellStyle(HSSFCellStyle style);                //设置单元格样式
//
//         OutputStream ouputStream;
//         try {
//             response.setContentType("application/vnd.ms-excel");
//             response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"), "ISO8859-1"));  // 设置下载时客户端Excel的名称，filename包含后缀.xls
//             ouputStream = response.getOutputStream();
//             wb.write(ouputStream);
//             ouputStream.flush();
//             ouputStream.close();
//         } catch (UnsupportedEncodingException e) {
//             e1.printStackTrace();
//         }
//     }
//
//     public List getImportExcel(MultipartHttpServletRequest request) {
//         MultipartFile file = request.getFile("importExcel");
//         InputStream is = file.getInputStream();                //获取文件输入流
//
//         POIFSFileSystem fs = new POIFSFileSystem(is);          //创建文件系统
//         HSSFWorkbook wb = new HSSFWorkbook(fs);                //创建工作簿workbook
//
//         int number = wb.getNumberOfSheets();                   //获取当前工作簿中的工作表的数量
//         HSSFSheet sheet wb.getSheetAt(int index);		       //根据索引获取工作簿的第几个工作表
//
//         int lastRowNum = sheet.getLastRowNum();                //获取工作表的最后一行
//         HSSFRow row = sheet.getRow(int rowIndex);              //根据索引获取工作表的第几行
//
//         HSSFCell cell = row.getCell(int cellnum);              //获取单元格，从0开始
//
//         int type = cell.getCellType();                         //获取单元格值的类型（返回值对应Cell接口的int类型静态常量，如Cell.CELL_TYPE_BOOLEAN表示布尔类型）
//         boolean boolValue = cell.getBooleanCellValue();        //获取布尔类型的单元格的值
//         double doubleValue = cell.getNumericCellValue();       //获取数值类型的单元格的值
//         String strValue = cell.getStringCellValue();           //获取字符类型的单元格的值
//
//     }
// }
