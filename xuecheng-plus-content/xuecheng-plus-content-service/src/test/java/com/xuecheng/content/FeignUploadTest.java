package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 测试使用feign远程上传文件
 * @date 2024/3/14 21:23
 */
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    @Test
    public void test() throws IOException {
        //将file转成MultipartFile
        File file = new File("F:\\120.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String upload = mediaServiceClient.upload(multipartFile, "course/120.html");
        if (upload==null){
            System.out.println("走降级逻辑");
        }
    }
}
