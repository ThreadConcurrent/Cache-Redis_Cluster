package com.roncoo.eshop.inventory.dao.impl;

import com.roncoo.eshop.inventory.dao.RedisDAO;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

/**
 * @author WangCheng
 * @describe
 * @create 2020-04-27 16:20
 */
@Repository("redisDAO")
public class RedisDAPImpl implements RedisDAO{

    @Resource
    private JedisCluster jedisCluster;

    @Override
    public void set(String key, String value) {
        jedisCluster.set(key,value);
    }

    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }
}
