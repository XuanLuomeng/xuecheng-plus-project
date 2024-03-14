package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/14 21:39
 */
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    /**
     * 拿到熔断异常信息
     * @param throwable
     * @return
     */
    @Override
    public MediaServiceClient create(Throwable throwable) {

        return new MediaServiceClient() {
            //发生熔断上层服务调用此方法执行降级逻辑
            @Override
            public String upload(MultipartFile filedata, String objectName) throws IOException {
                log.debug("远程调用上传文件的接口发生熔断:{}",throwable.toString(),throwable);
                return null;
            }
        };
    }
}
