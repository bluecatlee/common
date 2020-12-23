package com.github.bluecatlee.common.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

/**
 * Ftp工具类 不支持sftp
 */
@Slf4j
public class FtpUtils {

    /**
     * 是否打印命令返回信息
     */
    private static boolean PRINT_RETURN_MESSAGE = true;


    /**
     * 创建连接并匿名登陆
     */
    public static FTPClient createFtpClientAnonymous(String server, int port) {
        return createFtpClient(server, port, "anonymous", "");
    }

    /**
     * 创建连接并登录ftp服务器
     */
    public static FTPClient createFtpClient(String server, int port, String userName, String userPassword) {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.connect(server, port);
            boolean result = ftpClient.login(userName, userPassword);
            printMessage(ftpClient);
            if (result) {
                result = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                printMessage(ftpClient);
            } else {
                throw new RuntimeException(">>> 登陆失败: " + ftpClient.getReplyString());
            }
        } catch (IOException e) {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                    printMessage(ftpClient);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return ftpClient;
    }

    /**
     * ftp 上传文件
     */
    public static boolean uploadFile(FTPClient ftp, String path, String filename, InputStream input) {
        boolean flag = false;
        ftp.setControlEncoding("GBK");
        try {
            // todo 如果是本地用户且配置了local_root，返回的工作目录就是local_root  但是本地用户实际有权限的目录是其在系统中用户目录 两个目录不一致 这是ftp服务端配置的问题吗
//            String workingDirectory = ftp.printWorkingDirectory();
//            printMessage(ftp);
//            System.out.println(workingDirectory);
//            ftp.makeDirectory(path);              // 一般目录应该提前创建好 尽量分配较少的权限给用户
//            printMessage(ftp);
            ftp.changeWorkingDirectory(path);
            printMessage(ftp);
            flag = ftp.storeFile(filename, input);
            printMessage(ftp);
            ftp.logout();
            printMessage(ftp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return flag;
    }

    private static void printMessage(FTPClient ftp) {
        if (PRINT_RETURN_MESSAGE) {
            log.info(ftp.getReplyString());
        }
    }

    public static void main(String[] args) {
//        FTPClient client = createFtpClientAnonymous("10.203.1.44", 21);
        FTPClient client = createFtpClient("172.17.128.11", 21, "ftptst", "Ftp9yxm8V");

        try {
            FileInputStream in = new FileInputStream(new File("C:/Users/admin/Desktop/test.txt"));
            uploadFile(client, "/data/ftptst", "a.txt", in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
