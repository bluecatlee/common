package com.github.bluecatlee.common.file;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

public class MyFileUtils {

    // public static boolean createFile(String path, String fileName, String filecontent) {
    //     Boolean bool = false;
    //     File file = new File(path);
    //     try {
    //         if (!file.exists()) {
    //             // 创建文件
    //             file.createNewFile();
    //             bool = true;
    //             System.out.println("success create file,the file is " + path);
    //             // 写入内容
    //             writeFileContent(path, filecontent);
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return bool;
    // }
    //
    // public static boolean writeFileContent(String filepath, String newstr) throws IOException {
    //     Boolean bool = false;
    //     String filein = newstr + "\r\n";
    //     String temp = "";
    //
    //     FileInputStream fis = null;
    //     InputStreamReader isr = null;
    //     BufferedReader br = null;
    //     FileOutputStream fos = null;
    //     PrintWriter pw = null;
    //     try {
    //         File file = new File(filepath);
    //
    //         fis = new FileInputStream(file);
    //         isr = new InputStreamReader(fis);
    //         br = new BufferedReader(isr);
    //         StringBuffer buffer = new StringBuffer();
    //
    //         for (int i = 0; (temp = br.readLine()) != null; i++) {
    //             buffer.append(temp);
    //             buffer = buffer.append(System.getProperty("line.separator"));
    //         }
    //         buffer.append(filein);
    //
    //         fos = new FileOutputStream(file);
    //         pw = new PrintWriter(fos);
    //         pw.write(buffer.toString().toCharArray());
    //         pw.flush();
    //         bool = true;
    //     } catch (Exception e) {
    //         // TODO: handle exception
    //         e.printStackTrace();
    //     } finally {
    //         if (pw != null) {
    //             pw.close();
    //         }
    //         if (fos != null) {
    //             fos.close();
    //         }
    //         if (br != null) {
    //             br.close();
    //         }
    //         if (isr != null) {
    //             isr.close();
    //         }
    //         if (fis != null) {
    //             fis.close();
    //         }
    //     }
    //     return bool;
    // }
    //
    // public static String readFileContent(String path) throws IOException {
    //     File file = new File(path);
    //     FileReader reader = new FileReader(file);// 定义一个fileReader对象，用来初始化BufferedReader
    //     BufferedReader bReader = new BufferedReader(reader);// new一个BufferedReader对象，将文件内容读取到缓存
    //     StringBuilder sb = new StringBuilder();// 定义一个字符串缓存，将字符串存放缓存中
    //     String s = "";
    //     while ((s = bReader.readLine()) != null) {// 逐行读取文件内容，不读取换行符和末尾的空格
    //         sb.append(s + "\n");// 将读取的字符串添加换行符后累加存放在缓存中
    //         // System.out.println(s);
    //     }
    //     bReader.close();
    //     return sb.toString();
    // }

    /**
     * 下载文件(ie浏览器不行)
     * @throws Exception
     */
    @Deprecated
    public static ResponseEntity<byte[]> download0(String filePath) throws Exception {
        // File file = new File(path + File.separator + filename);
        File file = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
        //下载显示的文件名，解决中文名称乱码问题
        String downloadFileName = new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
        //通知浏览器以attachment下载
        headers.setContentDispositionFormData("attachment", downloadFileName);
        //application/octet-stream ： 二进制流数据
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }


    /**
     * 下载文件
     * @throws Exception
     */
    public static void download(String filePath, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        String path = "";
        if (filePath.contains("/")) {
            path = filePath.substring(0, filePath.lastIndexOf("/"));
        }
        download(path, filename, request, response);
    }

    /**
     * 下载文件
     * @throws Exception
     */
    public static void download(String path, String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //读取文件
        InputStream in = new FileInputStream(path + File.separator + filename);

        //处理不同浏览器下载中文文件名的问题
        String header = request.getHeader("User-Agent").toUpperCase();
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", "%20");    //IE下载文件名空格变+号问题
        } else {
            filename = new String(filename.getBytes(), "ISO8859-1");
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType("application/force-download");         //应用程序强制下载
        response.setContentLength(in.available());

        OutputStream out = response.getOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        while((len = in.read(b))!=-1){
            out.write(b, 0, len);
        }
        out.flush();
        out.close();
        in.close();
    }

}
