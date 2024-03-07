package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:TODO
 * @date 2024/3/6 23:46
 */
public interface CourseTeacherService {
    /**
     * 通过课程计划id获取课程教师
     *
     * @param courseId
     * @return
     */
    public List<CourseTeacher> getCourseTeacherList(Long courseId);

    /**
     * 添加/修改课程计划教师
     *
     * @param courseTeacher
     */
    public void saveCourseTeacher(CourseTeacher courseTeacher);

    /**
     * 删除课程计划教师
     *
     * @param courseId  课程id
     * @param teacherId 教师id
     */
    public void deleteCourseTeacher(Long courseId, Long teacherId);
}
