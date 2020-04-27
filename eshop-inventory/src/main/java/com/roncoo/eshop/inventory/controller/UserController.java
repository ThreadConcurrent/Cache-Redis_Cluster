package com.roncoo.eshop.inventory.controller;

import com.roncoo.eshop.inventory.model.User;
import com.roncoo.eshop.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户Controller控制器
 * @author WangCheng
 * @describe
 * @create 2020-04-27 16:00
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * @return
     */
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public User getUserInfo(){
        User user = userService.findUserInfo();
        return user;
    }

    /**
     * 从Redis Cluster缓存中获取用户信息
     * @return
     */
    @RequestMapping("/getCachedUserInfo")
    @ResponseBody
    public User getCachedUserInfo(){
        User user = userService.getCachedUserInfo();
        return user;
    }


}
