package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:师资管理
 * @date 2024/3/6 23:45
 */
@Api(value = "课程计划师资管理接口", tags = "课程计划师资管理接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("查询课程计划教师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacherList(@PathVariable Long courseId) {
        List<CourseTeacher> courseTeacherList = courseTeacherService.getCourseTeacherList(courseId);
        return courseTeacherList;
    }

    @ApiOperation("添加课程计划教师")
    @PostMapping("/courseTeacher")
    public void addCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        courseTeacherService.saveCourseTeacher(courseTeacher);
    }

    @ApiOperation("修改课程计划教师")
    @PutMapping("/courseTeacher")
    public void putCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        courseTeacherService.saveCourseTeacher(courseTeacher);
    }

    @ApiOperation("删除课程计划教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseTeacherService.deleteCourseTeacher(courseId, teacherId);
    }
}
