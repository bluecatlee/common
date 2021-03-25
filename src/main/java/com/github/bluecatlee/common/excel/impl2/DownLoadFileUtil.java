package com.github.bluecatlee.common.excel.impl2;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;

public class DownLoadFileUtil {

	public static void downloadExcel(HttpServletRequest req, HttpServletResponse res, SXSSFWorkbook wb, String fileName) {
        if (wb == null) {
            return;
        }
        ServletOutputStream os;
        try {
        	
            res.setContentType("application/vnd.ms-excel");
            // 以保存或者直接打开的方式把Excel返回到页面
            String agent = req.getHeader("USER-AGENT");
            if(agent != null && agent.toLowerCase().contains("firefox")) {
                fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64((fileName+".xlsx").getBytes("UTF-8")))) + "?=";
            }else {
                fileName = URLEncoder.encode(fileName+".xlsx", "UTF-8");
            }
            res.setHeader("Content-disposition","attachment; filename=" + fileName );
            os = res.getOutputStream();

            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
           e.printStackTrace();
        }

    }
	public static void downloadFile(HttpServletRequest req, HttpServletResponse res, File file, String fileName) {
        ServletOutputStream os;
        try {
        	
            res.setContentType("application/vnd.ms-excel");
            // 以保存或者直接打开的方式把Excel返回到页面
            String agent = req.getHeader("USER-AGENT");
            if(agent != null && agent.toLowerCase().contains("firefox")) {
                fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64((fileName).getBytes("UTF-8")))) + "?=";
            }else {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            }
            res.setHeader("Content-disposition","attachment; filename=" + fileName );
            os = res.getOutputStream();

            InputStream in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
            	os.write(tempbyte);
            }
            in.close();
           
            os.flush();
            os.close();
        } catch (Exception e) {
           e.printStackTrace();
        }

    }
}
