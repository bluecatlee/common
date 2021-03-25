package com.github.bluecatlee.common.excel.impl1;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

/**
 * excel导出通用类
 */
public abstract class AbstractPoiExcel {

	@Autowired
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPoiExcel.class);

	public abstract void setData(HSSFWorkbook excel);

	public abstract String getTemplate();

	public HSSFWorkbook export() {
		String template = this.getTemplate();
		LOGGER.info("模版路径:{}", template);

		if (StringUtils.isEmpty(template)) 
			throw new RuntimeException("未找到导出模版，请联系管理员");

		try {
			ClassPathResource resource = new ClassPathResource(template);
			InputStream inputStream = resource.getInputStream();
			
			if(inputStream == null) {
				LOGGER.info("模版文件不存在：{}", template);	
			}
			
			HSSFWorkbook excel = new HSSFWorkbook(inputStream);
			this.setData(excel);
			return excel;
		} catch (Exception e) {
			LOGGER.error("系统异常：{}" + e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 合并单元格
	 * 
	 * 这个就是合并单元格 参数说明：1：开始行 2：结束行 3：开始列 4：结束列 比如我要合并 第二行到第四行的 第六列到第八列
	 * sheet.addMergedRegion(new CellRangeAddress(1,3,5,7));
	 */
	protected void mergeCell(HSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
		sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, startCol, endCol));
	}

	/**
	 * 创建四周边框线的单元格
	 * @param row
	 * @param index
	 * @param style
	 * @return
	 */
	protected HSSFCell createStyleCell(HSSFRow row, int index, HSSFCellStyle style) {
		HSSFCell cell = row.createCell(index);
		cell.setCellStyle(style);
		return cell;
	}

	/**
	 * 创建单元格四周边框线以及设置字体格式
	 * 
	 * @param excel
	 * @return
	 */
	protected HSSFCellStyle getRoundBorderStyle(HSSFWorkbook excel) {
		HSSFFont font = excel.createFont();//创建字体
		font.setBold(true);//是否加粗字体
		HSSFCellStyle s = excel.createCellStyle();
		s.setFont(font);
		s.setAlignment(HorizontalAlignment.CENTER);//水平居中
		s.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
		s.setBorderBottom(BorderStyle.THIN);
		s.setBorderRight(BorderStyle.THIN);
		s.setBorderTop(BorderStyle.THIN);
		s.setBorderLeft(BorderStyle.THIN);
		return s;
	}

}
