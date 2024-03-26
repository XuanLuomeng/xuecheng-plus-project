package com.xuecheng.learning.service;

import com.xuecheng.base.model.RestResponse;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 在线学习相关的接口
 * @date 2024/3/26 22:15
 */
public interface LearningService {

    /**
    * @description 获取教学视频
    * @Param [userId 用户id, courseId 课程id, teachplanId 课程计划id, mediaId 视频文件id]
    * @return RestResponse<String>
    * @author LuoXuanwei
    * @date 2024/3/26 22:17
    */
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
