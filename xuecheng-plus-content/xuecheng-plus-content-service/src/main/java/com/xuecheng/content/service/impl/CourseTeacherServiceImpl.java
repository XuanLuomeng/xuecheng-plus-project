package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:TODO
 * @date 2024/3/6 23:46
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(wrapper);
        return courseTeachers;
    }

    @Override
    public void saveCourseTeacher(CourseTeacher courseTeacher) {
        if (courseTeacher.getTeacherName() != null && courseTeacher.getPosition() != null && courseTeacher.getTeacherName() != "" && courseTeacher.getPosition() != "") {
            courseTeacher.setCreateDate(LocalDateTime.now());
            int save = 0;
            if (courseTeacher.getId() != null) {
                save = courseTeacherMapper.updateById(courseTeacher);
            } else {
                save = courseTeacherMapper.insert(courseTeacher);
            }
            if (save == 0) {
                XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
            }
        } else {
            XueChengPlusException.cast(CommonError.REQUEST_NULL);
        }
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId, teacherId);
        int delete = courseTeacherMapper.delete(wrapper);
        if (delete == 0) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
