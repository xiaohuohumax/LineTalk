package com.example.line_talk_background.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Hello {

    @RequestMapping("/")
    @ResponseBody
    String hello() {
        return "hello world!";
    }
}
