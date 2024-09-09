package com.lgs.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class OOSUtil {
    private static final String ENDPOINT = "oss-cn-qingdao.aliyuncs.com";
    private static String AccessKeyID; //accID
    private static String AccessKeySecret;//ACCSecret
    private static final String BUKETNAME="lgs-aliyun-oos";//仓库名称
    private static final String FolderPrefix="pic";// 目标文件夹
//    https://lgs-aliyun-oos.oss-cn-qingdao.aliyuncs.com/
    private static final String SUFFER_URL ="https://"+BUKETNAME+"."+ENDPOINT; //上传成功返回的url，仓库名称+节点地址

    @Value("${aliyun.AccessKeyID}")
    public void setAccessKeyID(String accessKeyID) {
        AccessKeyID = accessKeyID;
    }
    @Value("${aliyun.AccessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }
    public static OSS getOSSClient() {
        // 创建OSSClient实例。
        OSSClient ossClient=new OSSClient(ENDPOINT,AccessKeyID,AccessKeySecret);
        return ossClient;
    }
    public static Map<String,Object> upload(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        String suffix = fileName.substring(index);
        //从 1970 年 1 月 1 日 UTC 时间零点到现在的毫秒数
        String newFileName = System.currentTimeMillis() + suffix;//新文件名
//        文件上传文件的路径
        String dir = FolderPrefix + "/";
//        文件上传文件的路径+文件名
        String uploadUrl = dir + newFileName;
        OSS ossClient = getOSSClient();
//        获取文件输入流
        InputStream inputStream=null;
        try {
            inputStream=file.getInputStream();
            // 上传
            ossClient.putObject(BUKETNAME,uploadUrl,inputStream);
            return Map.of( "success", true,"info",SUFFER_URL +"/"+ uploadUrl);
        } catch (IOException e) {
            return Map.of( "success", false,"info", e.getLocalizedMessage());
        } finally {
            // 关闭客户端(关闭后台线程)
            ossClient.shutdown();
        }
    }
}
