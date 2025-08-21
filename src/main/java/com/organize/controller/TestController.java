package com.organize.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
