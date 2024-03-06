package com.xuecheng.content;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description:TODO
 * @date 2024/3/5 21:37
 */
@SpringBootTest
public class CourseCategoryServiceTests {
    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    public void testCourseCategoryService(){
        List<CourseCategoryTreeDto> categoryTreeDtoList = courseCategoryService.queryTreeNodes("1");
        System.out.println(categoryTreeDtoList);
    }
}
