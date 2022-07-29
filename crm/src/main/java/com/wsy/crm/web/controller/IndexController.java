package com.wsy.crm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//一般来说，只有页面都在同一个目录下，处理相关逻辑以及请求跳转的controller使用一个类，页面在不同目录下，应当使用不同的controller实现
public class IndexController {
    /**理论上，给Controller方法分配url，应该是http://127.0.0.1:8080/crm/
    *为了简便，协议://ip:port/工程名 必须省去，用/代表应用根目录下的/
    *
     */

    @RequestMapping("/")
    public String index(){
        //跳转方式为请求转发
        return "index";
    }
}
