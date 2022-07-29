package com.wsy.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    /**
     * 通过该控制器跳转至/workbench/main/index.jsp页面
     * @return
     */
    @RequestMapping("/workbench/main/index.do")
    public String index(){


        return "/workbench/main/index";
    }
}
