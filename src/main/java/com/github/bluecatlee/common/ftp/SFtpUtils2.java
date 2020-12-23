package com.github.bluecatlee.common.ftp;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * 升级版SFtpUtils 支持多个并发ssh连接和多个ftp会话同时存在
 * @usage 作为多例bean注入 或者直接new使用.  使用完必须手动断开
 * @date 2020/12/22
 */
//@Service
//@Scope("prototype")
@Slf4j
public class SFtpUtils2 {

    private Session sshSession;
    private ChannelSftp sftp;
    private int timeout;        // 超时时间 单位s

    // 必须设置超时时间 否则会一直等待
//    public SFtpUtils2() {}

    public SFtpUtils2(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 连接
     */
    public void connect(String server, int port, String userName, String userPassword) throws JSchException {
//        FtpJSch ftp = new FtpJSch();
        JSch jsch = new JSch();
        sshSession = jsch.getSession(userName, server, port);
        sshSession.setPassword(userPassword);
        Properties sshConfig = new Properties();
        //关闭严格主机密钥检查
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        //开启sshSession链接
        sshSession.connect(timeout);
        //获取sftp通道
        Channel channel = sshSession.openChannel("sftp");
        //开启
        channel.connect();
        sftp = (ChannelSftp) channel;
    }

    /**
     * 上传 调用者关闭输入流（Closeable的对象由创建者关闭）
     */
    public void upload(String directory, String filename, InputStream input) throws SftpException {
        sftp.cd(directory);
        sftp.put(input, filename);
    }

    /**
     * 下载指定远程目录下的文件到本地
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException {
        FileOutputStream output = new FileOutputStream(new File(saveFile));
        sftp.cd(directory);
        sftp.get(downloadFile, output);
        try {
            output.close();
        } catch (IOException e) {
        }
    }

    /**
     * 查看指定目录下的内容 ls
     */
    public List<String> ls(String directory) throws SftpException {
        List<String> list = new ArrayList<>();
        Vector ls = sftp.ls(directory);
        if (ls != null && ls.size() > 0) {
            ls.forEach(v -> list.add(v.toString()));
        }
        return list;
    }

    /**
     * 删除指定目录下的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (sftp != null && sftp.isConnected()) {
            sftp.disconnect();
        }
        if (sshSession != null && sshSession.isConnected()) {
            sshSession.disconnect();
        }
    }

}