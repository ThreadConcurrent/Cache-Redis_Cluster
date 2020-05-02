package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.request.Request;

/**
 * 请求异步执行的Service
 * @author WangCheng
 * @describe
 * @create 2020-05-02 14:16
 */
public interface RequestAsyncProcessService {

    void process(Request request);



}
