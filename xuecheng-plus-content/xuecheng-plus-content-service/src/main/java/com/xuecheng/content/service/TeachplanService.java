package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:课程计划管理相关接口
 * @date 2024/3/6 21:36
 */
public interface TeachplanService {
    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return 课程计划列表
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增/修改/保存课程计划
     * @param saveTeachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachplanId
     */
    public void deleteTeachplan(Long teachplanId);

    /**
     * 移动课程计划
     * @param move 上/下移动
     * @param teachplanId 触发移动的课程计划id
     */
    public void moveTeachplan(String move,Long teachplanId);

    /**
     * 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
