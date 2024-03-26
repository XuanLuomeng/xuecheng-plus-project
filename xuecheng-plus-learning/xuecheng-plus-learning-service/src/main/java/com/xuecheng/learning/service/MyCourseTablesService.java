package com.xuecheng.learning.service;

import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 选课相关的接口
 * @date 2024/3/24 17:29
 */
public interface MyCourseTablesService {

    /**
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     * @description 添加选课
     * @Param [userId 用户id, courseId 课程id]
     * @author LuoXuanwei
     * @date 2024/3/24 17:30
     */
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @return com.xuecheng.learning.model.dto.XcCourseTablesDto
     * @description 判断学习资格
     * @Param [userId, courseId]
     * @author LuoXuanwei
     * @date 2024/3/24 18:11
     */
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    /**
    * @description 保存选课成功状态
    * @Param [chooseCourseId]
    * @return boolean
    * @author LuoXuanwei
    * @date 2024/3/26 21:28
    */
    public boolean saveChooseCourseSuccess(String chooseCourseId);
}
