package com.github.bluecatlee.common.ftp;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * SFtpUtils
 * @usage 每个操作都需要重新获取连接 getFtpClient()。 并发不安全 外部调用者需要加同步
 * @date 2020/12/22
 */
@Slf4j
public class SFtpUtils {

    // 仅支持同一时刻存在一个ssh会话
    private static Session sshSession = null;

    /**
     * 连接
     */
    public static ChannelSftp getFtpClient(String server, int port, String userName, String userPassword) {
//        FtpJSch ftp = new FtpJSch();
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(userName, server, port);
            sshSession.setPassword(userPassword);
            Properties sshConfig = new Properties();
            //关闭严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            //开启sshSession链接
            sshSession.connect();
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return sftp;
    }

    /**
     * 上传
     */
    public static void upload(ChannelSftp sftp, String directory, String filename, InputStream input) {
        try {
            sftp.cd(directory);
            sftp.put(input, filename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
            if (sshSession != null && sshSession.isConnected()) {
                sshSession.disconnect();
            }
        }
    }

    /**
     * 下载指定远程目录下的文件到本地
     */
    public static void download(ChannelSftp sftp, String directory, String downloadFile, String saveFile) {
        try {
            sftp.cd(directory);
            sftp.get(downloadFile, new FileOutputStream(new File(saveFile)));
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
            if (sshSession != null && sshSession.isConnected()) {
                sshSession.disconnect();
            }
        }
    }

    /**
     * 查看指定目录下的内容 ls
     */
    public static List<String> ls(ChannelSftp sftp, String directory) {
        List<String> list = new ArrayList<>();
        try {
            Vector ls = sftp.ls(directory);
            if (ls != null && ls.size() > 0) {
                ls.forEach(v -> list.add(v.toString()));
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
            if (sshSession != null && sshSession.isConnected()) {
                sshSession.disconnect();
            }
        }
        return list;
    }

    /**
     * 删除指定目录下的文件
     */
    public static void delete(ChannelSftp sftp, String directory, String deleteFile) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
            if (sshSession != null && sshSession.isConnected()) {
                sshSession.disconnect();
            }
        }
    }

}