package com.niiish32x.lithefs.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello(){
        System.out.println("hello");
        return "hello";
    }
}
