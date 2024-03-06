package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:课程计划信息模型类
 * @date 2024/3/6 20:58
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {
    //与媒资管理的信息
    private TeachplanMedia teachplanMedia;

    //小章节
    private List<Teachplan> teachPlanTreeNodes;
}
