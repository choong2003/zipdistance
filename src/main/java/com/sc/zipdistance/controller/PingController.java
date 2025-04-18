package com.sc.zipdistance.controller;

import com.sc.zipdistance.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Profile("dev")
@RestController
@RequestMapping("/api/v1/dev")
public class PingController {

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping("/ping")
    public String healthCheck() {
        return "This is healthy.";
    }

}
