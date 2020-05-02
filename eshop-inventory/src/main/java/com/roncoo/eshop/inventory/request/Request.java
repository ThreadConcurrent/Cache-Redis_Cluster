package com.roncoo.eshop.inventory.request;

/**
 * 请求接口
 * @author WangCheng
 * @describe
 * @create 2020-05-02 9:38
 */
public interface Request {

    void process();

    Integer getProductId();

    boolean isForceRefresh();

}
