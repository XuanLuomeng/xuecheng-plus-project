package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 课程发布的任务类
 * @date 2024/3/13 21:02
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    /**
     * 任务调度入口
     *
     * @throws Exception
     */
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //调用抽象类的方法执行方法
        process(shardIndex, shardTotal, "course_public", 30, 60);

    }

    /**
     * 执行课程发布的任务逻辑,如果此方法抛出异常说明任务执行失败
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        //从mqMessage拿到课程id
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        //课程静态化上传到minio
        generateCourseHtml(mqMessage, courseId);
        //向elasticsearch写索引数据
        saveCourseIndex(mqMessage, courseId);
        //向redis写缓存

        //返回true表示任务完成
        return true;
    }

    /**
     * 生成课程静态化页面并上传至文件系统
     *
     * @param mqMessage
     * @param courseId
     */
    private void generateCourseHtml(MqMessage mqMessage, Long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        //做任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            log.debug("课程静态化任务完成，无需处理...");
            return;
        }
        //开始进行课程静态化 生成html页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file==null){
            XueChengPlusException.cast("生成的静态页面为null");
        }
        //将html上传到minio
        coursePublishService.uploadCourseHtml(courseId, file);
        //..任务处理完成写任务状态为完成
        mqMessageService.completedStageOne(taskId);
    }

    /**
     * 将课程信息缓存至redis 第二个阶段任务
     *
     * @param mqMessage
     * @param courseId
     */
    public void saveCourseCache(MqMessage mqMessage, long courseId) {
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二个阶段状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        //任务幂等性处理
        if (stageTwo > 0) {
            log.debug("课程索引信息已写入，无需执行...");
            return;
        }
        //查询课程信息，调用搜索服务添加索引...

        //完成本阶段的任务
        mqMessageService.completedStageTwo(taskId);
    }

    /**
     * 保存课程索引信息
     *
     * @param mqMessage
     * @param courseId
     */
    public void saveCourseIndex(MqMessage mqMessage, long courseId) {

    }
}
