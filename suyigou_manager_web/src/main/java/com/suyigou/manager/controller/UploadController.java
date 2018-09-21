package com.suyigou.manager.controller;

import entity.ResultInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public ResultInfo upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String exName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String path = fastDFSClient.uploadFile(file.getBytes(), exName, null);
            String url = FILE_SERVER_URL + path;
            return new ResultInfo(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "上传失败");
        }
    }
}
