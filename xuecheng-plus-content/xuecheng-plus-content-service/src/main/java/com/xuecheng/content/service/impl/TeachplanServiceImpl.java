package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:TODO
 * @date 2024/3/6 21:38
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count + 1;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程id判断是新增还是修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            //确定排序字段,找到它的同级节点个数，排序字段就是个数+1
            Long parentId = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            teachplan.setOrderby(getTeachplanCount(courseId, parentId));
            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            //将参数复制到teachplan
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long teachplanId) {
        //通过课程计划id查询parentId与之相同的，若为空则可删除章节
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, teachplanId);
        Integer count = teachplanMapper.selectCount(wrapper);
        if (count != 0) {
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        } else {
            //判断grade层级不为1则同时删除小章节内的media资源
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            if (teachplan.getGrade() == 1) {
                int delete = teachplanMapper.deleteById(teachplanId);
                if (delete == 0) {
                    XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
                }
            } else {
                LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
                //首先判断是否有media资源
                Integer integer = teachplanMediaMapper.selectCount(queryWrapper);
                int deleteMedia = teachplanMediaMapper.delete(queryWrapper);
                if (deleteMedia == 0 && integer != 0) {
                    XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
                } else {
                    int delete = teachplanMapper.deleteById(teachplanId);
                    if (delete == 0) {
                        XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
                    }
                }
            }
        }
    }

    //move为0时上移，为1时下移
    @Override
    public void moveTeachplan(String move, Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (move.equals("0")) {
            //当orderby为1时无法上移
            if (teachplan.getOrderby() != 1) {
                LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
                Integer orderby = teachplan.getOrderby();
                wrapper.eq(Teachplan::getOrderby, orderby - 1).eq(Teachplan::getParentid, teachplan.getParentid());
                Teachplan teachplanUp = teachplanMapper.selectOne(wrapper);

                teachplan.setOrderby(orderby - 1);
                teachplanMapper.updateById(teachplan);
                teachplanUp.setOrderby(orderby);
                teachplanMapper.updateById(teachplanUp);
            }
        } else {
            //当orderby+1时无法从数据库查询到内容无法下移
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            Integer orderby = teachplan.getOrderby();
            wrapper.eq(Teachplan::getOrderby, orderby + 1).eq(Teachplan::getParentid, teachplan.getParentid());
            Teachplan teachplanDown = teachplanMapper.selectOne(wrapper);
            if (teachplanDown != null) {
                teachplan.setOrderby(orderby + 1);
                teachplanMapper.updateById(teachplan);
                teachplanDown.setOrderby(orderby);
                teachplanMapper.updateById(teachplanDown);
            }
        }
    }
}
