package com.codetest.global.util;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping
    public String home() {
        return "home";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
