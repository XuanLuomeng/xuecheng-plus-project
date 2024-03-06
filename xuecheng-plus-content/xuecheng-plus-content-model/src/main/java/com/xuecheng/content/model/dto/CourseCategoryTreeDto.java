package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:TODO
 * @date 2024/3/5 17:52
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory {
    //子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
