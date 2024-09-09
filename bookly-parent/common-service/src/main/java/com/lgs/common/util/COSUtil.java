package com.lgs.common.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class COSUtil {
    private static  final String bucketName = "lgsqqcos-1328892911";
    private static String secretId;
    private static String secretKey;
    private static final String region = "ap-beijing";
    private static final String folderPrefix ="pic";
    //注入值


    @Value("${tencent.secretId}")
    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    @Value("${tencent.secretKey}")
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    /**
     * 获取配置信息
     */
    public static COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }
    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception https://lgsqqcos-1328892911.cos.ap-beijing.myqcloud.com/pic/555.jpg
     */
    public static Map<String,Object> upload(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        String suffix = fileName.substring(index);
        //从 1970 年 1 月 1 日 UTC 时间零点到现在的毫秒数
        String newFileName = System.currentTimeMillis() + suffix;
        String dir = folderPrefix + "/";
        String key = dir + newFileName;
        COSClient cosclient = getCosClient();
        File localFile = null;
        try {
            localFile = File.createTempFile("temp", null);
            file.transferTo(localFile);
            // 指定要上传到 COS 上的路径
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            return Map.of( "success", true,"info","https://lgsqqcos-1328892911.cos.ap-beijing.myqcloud.com" +'/' + key);
        } catch (IOException e) {
            return Map.of( "success", false,"info", e.getLocalizedMessage());
        } finally {
            // 关闭客户端(关闭后台线程)
            cosclient.shutdown();
        }
    }
}