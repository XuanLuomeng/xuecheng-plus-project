package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 选课相关接口实现
 * @date 2024/3/24 17:31
 */
@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Autowired
    XcChooseCourseMapper chooseCourseMapper;

    @Autowired
    XcCourseTablesMapper courseTablesMapper;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Override
    @Transactional
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {

        //选课远程调用内容管理查询课程的收费规则
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            XueChengPlusException.cast("课程不存在");
        }
        //收费规则
        String charge = coursepublish.getCharge();
        //选课记录
        XcChooseCourse chooseCourse = null;
        //免费课程
        if ("201000".equals(charge)) {
            //如果免费课程，会向选课记录表
//            XcChooseCourse chooseCourse = addFreeCourse(userId, coursepublish);
            chooseCourse = addCourse(userId, coursepublish, "700001", "701001");
            //向我的课程表写
            XcCourseTables xcCourseTables = addCourseTables(chooseCourse);
        } else {
            //如果收费课程，会向选课记录表写数据
//            XcChooseCourse xcChooseCourse = addChargeCourse(userId, coursepublish);
            chooseCourse = addCourse(userId, coursepublish, "700002", "701002");
        }

        //判断学生的学习资格
        XcCourseTablesDto xcCourseTablesDto = getLearningStatus(userId, courseId);

        //构造返回值
        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse, xcChooseCourseDto);
        //设置学习资格状态
        xcChooseCourseDto.setLearnStatus(xcCourseTablesDto.getLearnStatus());

        return xcChooseCourseDto;
    }

    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {
        //返回结果
        XcCourseTablesDto courseTablesDto = new XcCourseTablesDto();

        //查询我的课程表，如果查不到说明没有选课
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if (xcCourseTables == null) {
            courseTablesDto.setLearnStatus("702002");
            return courseTablesDto;
        }

        //如果查到了，判断是否过期，如果国企不能继续学习，可以继续学习
        boolean before = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (before) {
            courseTablesDto.setLearnStatus("702003");
            BeanUtils.copyProperties(xcCourseTables, courseTablesDto);
            return courseTablesDto;
        } else {
            courseTablesDto.setLearnStatus("702001");
            BeanUtils.copyProperties(xcCourseTables, courseTablesDto);
            return courseTablesDto;
        }
    }

    @Override
    public boolean saveChooseCourseSuccess(String chooseCourseId) {

        //根据选课id查询选课表
        XcChooseCourse chooseCourse = chooseCourseMapper.selectById(chooseCourseId);
        if (chooseCourse == null) {
            log.debug("接收购买课程的消息，根据选课id从数据库找不到选课记录，选课id:{}", chooseCourseId);
            return false;
        }

        //选课状态
        String status = chooseCourse.getStatus();

        //只有当未支付时才更新为已支付
        if ("701002".equals(status)) {
            //更新选课记录的状态为支付成功
            chooseCourse.setStatus("701001");
            int update = chooseCourseMapper.updateById(chooseCourse);
            if (update <= 0) {
                log.debug("添加选课记录失败:{}", chooseCourse);
                XueChengPlusException.cast("添加选课记录失败");
            }

            //向我的课程表插入记录
            XcCourseTables xcCourseTables = addCourseTables(chooseCourse);
            return true;
        }

        return false;
    }

    @Override
    public PageResult<XcCourseTables> mycoursetabls(MyCourseTableParams params) {
        //当前用户id
        String userId = params.getUserId();
        //当前页码
        int pageNo = params.getPage();
        //每页的记录数
        int size = params.getSize();

        Page<XcCourseTables> xcCourseTablesPage = new Page<>(pageNo, size);
        LambdaQueryWrapper<XcCourseTables> lambdaQueryWrapper = new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId);

        Page<XcCourseTables> result = courseTablesMapper.selectPage(xcCourseTablesPage, lambdaQueryWrapper);

        //数据列表
        List<XcCourseTables> records = result.getRecords();
        //总记录数
        long total = result.getTotal();

        PageResult<XcCourseTables> pageResult = new PageResult<>(records, total, pageNo, size);

        return pageResult;
    }

    /**
     * @return com.xuecheng.learning.model.po.XcChooseCourse
     * @description 添加免费课程, 免费课程加入选课记录表、我的课程表(可与addChargeCourse类合并)
     * @Param [userId, coursepublish]
     * @author LuoXuanwei
     * @date 2024/3/24 17:37
     */
    @Deprecated
    public XcChooseCourse addFreeCourse(String userId, CoursePublish coursepublish) {
        //课程id
        Long courseId = coursepublish.getId();
        //判断，如果存在免费的课程记录且选课状态为成功，直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, userId)
                //免费课程
                .eq(XcChooseCourse::getOrderType, "700001")
                //选课成功
                .eq(XcChooseCourse::getStatus, "701001");

        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据

        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        //免费课程
        chooseCourse.setOrderType("700001");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(365);
        //选课成功
        chooseCourse.setStatus("701001");
        //有效期的开始时间
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        //有效期的结束时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) {
            XueChengPlusException.cast("添加选课记录失败");
        }

        return chooseCourse;
    }

    /**
     * @return com.xuecheng.learning.model.po.XcChooseCourse
     * @description 添加收费课程(可与addFreeCourse类合并)
     * @Param [userId, coursepublish]
     * @author LuoXuanwei
     * @date 2024/3/24 17:37
     */
    @Deprecated
    public XcChooseCourse addChargeCourse(String userId, CoursePublish coursepublish) {
        //课程id
        Long courseId = coursepublish.getId();
        //判断，如果存在收费的课程记录且选课状态为待支付，直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, userId)
                //收费课程
                .eq(XcChooseCourse::getOrderType, "700002")
                //待支付
                .eq(XcChooseCourse::getStatus, "701002");

        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据

        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        //收费课程
        chooseCourse.setOrderType("700002");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(365);
        //待支付
        chooseCourse.setStatus("701002");
        //有效期的开始时间
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        //有效期的结束时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) {
            XueChengPlusException.cast("添加选课记录失败");
        }

        return chooseCourse;
    }

    /**
     * @return com.xuecheng.learning.model.po.XcChooseCourse
     * @description 复用addChargeCourse和addFreeCourse
     * @Param [userId, coursepublish]
     * @author LuoXuanwei
     * @date 2024/3/24 18:07
     */
    public XcChooseCourse addCourse(String userId, CoursePublish coursepublish, String orderType, String status) {
        //课程id
        Long courseId = coursepublish.getId();
        //判断，如果存在收费的课程记录且选课状态为待支付，直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, userId)
                //收费课程
                .eq(XcChooseCourse::getOrderType, orderType)
                //待支付
                .eq(XcChooseCourse::getStatus, status);

        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据

        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        //收费课程
        chooseCourse.setOrderType(orderType);
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(365);
        //待支付
        chooseCourse.setStatus(status);
        //有效期的开始时间
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        //有效期的结束时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) {
            XueChengPlusException.cast("添加选课记录失败");
        }

        return chooseCourse;
    }

    /**
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @description 添加到我的课程表
     * @Param [xcChooseCourse]
     * @author LuoXuanwei
     * @date 2024/3/24 17:37
     */
    public XcCourseTables addCourseTables(XcChooseCourse xcChooseCourse) {

        //选课成功才可以像我的课程表添加
        String status = xcChooseCourse.getStatus();
        if (!"701001".equals(status)) {
            XueChengPlusException.cast("选课没有成功无法添加到课程表");
        }
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if (xcCourseTables != null) {
            return xcCourseTables;
        }

        xcCourseTables = new XcCourseTables();
        BeanUtils.copyProperties(xcChooseCourse, xcCourseTables);
        //记录选课表的
        xcCourseTables.setChooseCourseId(xcChooseCourse.getId());
        //选课类型
        xcCourseTables.setCourseType(xcChooseCourse.getOrderType());
        xcCourseTables.setUpdateDate(LocalDateTime.now());
        int insert = courseTablesMapper.insert(xcCourseTables);
        if (insert <= 0) {
            XueChengPlusException.cast("添加我的课程表失败");
        }

        return xcCourseTables;
    }

    /**
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @description 根据课程和用户查询我的课程表中某一门课程
     * @Param [userId, courseId]
     * @author LuoXuanwei
     * @date 2024/3/24 17:53
     */
    public XcCourseTables getXcCourseTables(String userId, Long courseId) {
        XcCourseTables xcCourseTables = courseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId).eq(XcCourseTables::getCourseId, courseId));
        return xcCourseTables;
    }
}
