package com.roncoo.eshop.inventory.mapper;

import com.roncoo.eshop.inventory.model.User;

/**
 * @author WangCheng
 * @describe
 * @create 2020-04-27 16:23
 */
public interface UserMapper {

    /**
     * 查询用户信息
     * @return
     */
    public User findUserInfo();

}
