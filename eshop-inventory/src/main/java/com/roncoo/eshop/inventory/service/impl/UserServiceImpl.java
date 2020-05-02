package com.roncoo.eshop.inventory.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.mapper.UserMapper;
import com.roncoo.eshop.inventory.model.User;
import com.roncoo.eshop.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author WangCheng
 * @describe
 * @create 2020-04-27 16:28
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisDAO redisDAO;

    @Override
    public User findUserInfo() {
        return userMapper.findUserInfo();
    }

    /**
     * 从缓存中获取用户信息
     * @return
     */
    @Override
    public User getCachedUserInfo() {
        redisDAO.set("cached_user_lisi", "{\"name\": \"lisi\", \"age\":28}");

        String userJSON = redisDAO.get("cached_user_lisi");
        JSONObject userJSONObject = JSONObject.parseObject(userJSON);

        User user = new User();
        user.setName(userJSONObject.getString("name"));
        user.setAge(userJSONObject.getInteger("age"));

        return user;
    }
}
