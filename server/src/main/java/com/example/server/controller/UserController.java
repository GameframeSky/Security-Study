package com.example.server.controller;

import com.example.server.entity.Account;
import com.example.server.utils.RestBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public RestBean<Account> me(@SessionAttribute("account") Account user){
        return RestBean.success(user);
    }
}