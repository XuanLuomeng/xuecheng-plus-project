package com.xuecheng.learning.service.impl;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/26 22:19
 */
@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {

        //查询课程信息
        CoursePublish coursePublish = contentServiceClient.getCoursepublish(courseId);
        //判断如果为null不再继续
        if (coursePublish == null) {
            return RestResponse.validfail("课程不存在");
        }

        //根据课程计划id(teachplanId)去查询课程计划信息，如果is_preview的值为1表示支持试学
        //可以从coursePublish对象中解析出课程计划信息去判断是否支持试学
        //TODO: 如果支持试学调用媒资服务查询视频的播放地址，返回

        //用户已登录
        if (StringUtils.isNotEmpty(userId)) {
            //获取学习资格
            XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();
            if ("702002".equals(learnStatus)) {
                return RestResponse.validfail("无法学习，因为没有选课或选课后没有支付");
            } else if ("702003".equals(learnStatus)) {
                return RestResponse.validfail("已过期，需要申请续期或重新支付");
            } else {
                //有资格学习，要返回视频的播放地址
                //远程调用媒资获取视频播放地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        }

        //如果用户没有登陆

        //取出课程的收费规则
        String charge = coursePublish.getCharge();

        if ("201000".equals(charge)) {
            //有资格学习，要返回视频的播放地址
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }

        return RestResponse.validfail("课程需要购买");
    }
}
