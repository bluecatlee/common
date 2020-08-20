package com.github.bluecatlee.common.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FastDFSUtils {

    // todo 配置外部化
    private static final String clientConfig = "fastdfs/fdfs_client.conf";
    private static final String trackerAddress = "192.168.10.233";
    private static final int trackerPort = 22122;

    private static final String storageAddress0 = "192.168.10.233";
    private static final int storagePort0 = 23000;
    private static final String storageAddress1 = "192.168.10.233";
    private static final int storagePort1 = 23001;
    private static final String group = "bluecat";


    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt");
        String[] result = upload_distributed(file);
        // byte[] bytes = FastDFSUtils.file2byte(file);
        // String[] result = upload_distributed(null, bytes, "txt");
        // String[] result = upload_specify_storage(storageAddress1, storagePort1, group, 1, bytes, "txt");

        // String[] result = upload_distributed("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt", "txt");
        if (result != null && result.length == 2) {
            // groupname
            System.out.println(result[0]);
            // filename
            System.out.println(result[1]);
        }

    }

    /**
     * 上传文件
     *      分布式存储，默认第一个分组，无后缀，
     * @param bytes
     * @return
     */
    public static String[] upload_distributed(byte[] bytes) {
        return upload_distributed(null, bytes, null);
    }

    public static String[] upload_distributed(String group, byte[] bytes, String ext) {
        try {
            ClientGlobal.init(clientConfig);
            // 实测使用trackerServer方式时 指定pathIndex无效 默认轮询
            StorageServer storageServer = new StorageServer(trackerAddress, trackerPort, 0);
            StorageClient storageClient = new StorageClient(storageServer, null);
            // if (StringUtils.isBlank(group)) {
            //     String[] result = storageClient.upload_file(bytes, ext, null);
            //     return result;
            // }
            // 如果不指定分组，默认选择第一个分组。多分组服务建议指定分组名称
            String[] result = storageClient.upload_file(group, bytes, ext, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传文件
     *      本地文件可以直接指定文件全路径名
     * @param localFilename
     * @return
     */
    public static String[] upload_distributed(String localFilename) {
        return upload_distributed(localFilename, (String)null);
    }

    public static String[] upload_distributed(String localFilename, String ext) {
        try {
            ClientGlobal.init(clientConfig);
            StorageServer storageServer = new StorageServer(trackerAddress, trackerPort, 0);
            StorageClient storageClient = new StorageClient(storageServer, null);
            String[] result = storageClient.upload_file(localFilename, ext, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] upload_distributed(File file) {
        return upload_distributed(null, file);
    }

    public static String[] upload_distributed(String group, File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        String ext = null;
        if (index != -1) {
            ext = fileName.substring(index + 1);
        }
        return upload_distributed(group, file2byte(file), ext);
    }

    public static String[] upload_distributed(MultipartFile file) {
        return upload_distributed(null, file);
    }

    public static String[] upload_distributed(String group, MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");
            String ext = null;
            if (index != -1) {
                ext = originalFilename.substring(index + 1);
            }
            return upload_distributed(group, bytes, ext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定特定的storage存储
     * @param storageAddress    storage地址
     * @param storagePort       starage端口
     * @param group             组名
     * @param pathIndex         路径索引
     * @param bytes             数据
     * @param ext               文件扩展名
     * @return
     */
    public static String[] upload_specify_storage(String storageAddress, int storagePort, String group, int pathIndex, byte[] bytes, String ext) {
        try {
            ClientGlobal.init(clientConfig);
            StorageServer storageServer = new StorageServer(storageAddress, storagePort, pathIndex);
            StorageClient storageClient = new StorageClient(null, storageServer);
            String[] result = storageClient.upload_file(group, bytes, ext, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] file2byte(File file){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

}
