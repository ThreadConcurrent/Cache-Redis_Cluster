package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.model.User;

/**
 * 用户Service
 * @author WangCheng
 * @describe
 * @create 2020-04-27 16:24
 */
public interface UserService {

    /**
     * 查询用户信息
     * @return 用户信息
     */
    public User findUserInfo();

    /**
     * 查询redis中用户缓存的信息
     * @return
     */
    public User getCachedUserInfo();

}
