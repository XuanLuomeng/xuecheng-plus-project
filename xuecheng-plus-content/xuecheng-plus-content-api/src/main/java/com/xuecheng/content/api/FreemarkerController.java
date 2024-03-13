package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/12 16:54
 */
@Controller
public class FreemarkerController {

    @GetMapping("/testfreemarker")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "admin");
        modelAndView.setViewName("test");
        return modelAndView;
    }
}
