package com.lgs.admin.backend.api;

import com.lgs.common.util.COSUtil;
import com.lgs.common.util.OOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/uploads", produces = "application/json;charset=utf-8")
public class UploadFileApi {
    @Autowired
    private COSUtil cosUtil;
    @Autowired
    private OOSUtil oosUtil;
    @PostMapping("/qcos")
    public Map<String,Object> uploadTen(@RequestParam("file") MultipartFile file) throws Exception {
        return cosUtil.upload(file);
    }
    @PostMapping("/alioos")
    public Map<String,Object> uploadAli(@RequestParam("file") MultipartFile file) throws Exception {
        return oosUtil.upload(file);
    }
    @PostMapping("/editor")
    public Map<String,Object> uploadEdi(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, Object> map = Map.of("url", cosUtil.upload(file).get("info"));
        return Map.of("errno", 0, "data", map);
    }
}
