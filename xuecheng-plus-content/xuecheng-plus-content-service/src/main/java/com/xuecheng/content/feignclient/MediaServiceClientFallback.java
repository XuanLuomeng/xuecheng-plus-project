package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/14 21:37
 */
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String upload(MultipartFile filedata, String objectName) throws IOException {

        return null;
    }
}
