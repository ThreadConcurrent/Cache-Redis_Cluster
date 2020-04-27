package com.roncoo.eshop.inventory.dao;

/**
 * redis 本身是支持各种各样的功能
 *
 * 可以做出来很多很多的花哨的功能
 *
 * @author WangCheng
 * @describe
 * @create 2020-04-27 16:06
 */
public interface RedisDAO {

    void set(String key,String value);

    String get(String key);


}
