package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/name")
    public @ResponseBody Map loginName(){
        System.out.println("接收请求");
        Map map=new HashMap();
        String name= SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        map.put("loginName",name);
        return map;
    }
}
