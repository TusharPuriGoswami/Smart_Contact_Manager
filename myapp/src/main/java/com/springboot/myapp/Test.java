package com.springboot.myapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Test {

    @RequestMapping("/test")
    @ResponseBody
    public String hello() {
        return "welcome to vs code Java";
    }
}
