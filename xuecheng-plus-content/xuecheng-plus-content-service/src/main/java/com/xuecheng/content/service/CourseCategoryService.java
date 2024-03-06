package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:TODO
 * @date 2024/3/5 21:00
 */
public interface CourseCategoryService {
    /**
     * 课程分类树形结构查询
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
