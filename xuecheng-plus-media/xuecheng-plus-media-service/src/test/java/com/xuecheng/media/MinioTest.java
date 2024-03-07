package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:TODO
 * @date 2024/3/7 22:37
 */
public class MinioTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //上传文件
    @Test
    public void upload() {
        try {
            //通过扩展名得到媒体资源类型 mimeType
            //根据扩展名取出mimeType
            ContentInfo contentInfo = ContentInfoUtil.findExtensionMatch(".jpg");
            String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (contentInfo != null) {
                mimeType = contentInfo.getMimeType();
            }

            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .filename("f:\\1.jpg")
                    .object("test/01/1.jpg")
                    .contentType(mimeType)//默认根据扩展名确定文件内容类型，也可以指定
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    //删除文件
    @Test
    public void delete() {
        try {

            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket("testbucket").object("1.jpg").build();

            minioClient.removeObject(removeObjectArgs);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }

    @Test
    //查询文件 从minio中下载
    public void test_getFile() throws Exception {
        GetObjectArgs testbucket = GetObjectArgs.builder().bucket("testbucket").object("test/01/1.jpg").build();
        //查询远程服务获取到一个流对象
        FilterInputStream inputStream = minioClient.getObject(testbucket);
        //指定输出流
        FileOutputStream outputStream = new FileOutputStream("f:\\2.jpg");
        IOUtils.copy(inputStream, outputStream);

        //校验文件的完整性对文件的内容进行md5
        String source_md5 = DigestUtils.md5Hex(inputStream);//minio中文件的md5

        String local_md5 = DigestUtils.md5Hex(new FileInputStream("f:\\2.jpg"));

        if (source_md5.equals(local_md5)){
            System.out.println("下载成功");
        }else {
            System.out.println("下载失败");
        }

    }
}
