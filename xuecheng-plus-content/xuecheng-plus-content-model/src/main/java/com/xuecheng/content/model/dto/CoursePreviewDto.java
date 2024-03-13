package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用于课程预览模型类
 * @date 2024/3/12 18:33
 */
@Data
public class CoursePreviewDto {
    //课程基本信息|课程营销信息
    private CourseBaseInfoDto courseBase;
    //课程计划信息
    private List<TeachplanDto> teachplans;
    //课程师资信息
}
