package org.dtristu.javaocr.usersecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminWebController {
    @GetMapping("/web/admin")
    public String adminPage (){
        return "adminPage";
    }
}
