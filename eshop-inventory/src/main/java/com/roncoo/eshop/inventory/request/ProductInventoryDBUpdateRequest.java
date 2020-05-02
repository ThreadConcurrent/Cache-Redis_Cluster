package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 比如说一个商品发生了交易,那么就要修改这个商品对应的库存
 *
 * 此时就会发送请求过来,要求修改库存,那么这个可能就是所谓的data update request,数据更新的请求
 *
 * cache aside pattern
 *
 * (1)删除缓存
 * (2)更新数据库
 *
 * 尽可能在电商的业务背景下用整个电商的业务把系统流程串起来,在项目里学习知识,而不是干讲各种解决方案,真实的场景
 *
 * 有大量的业务在里面,设计到几十个字段都有可能,过来的是一个什么请求,你得计算过后才知道库存是多少
 *
 * 一个电商系统,少则几十个人,多则几百人,少则百年一年,多则好多年。大量复杂的业务逻辑代码
 *
 * 几十个小时，撑死一百个小时，相当于一个工程师连续工作半个多月
 *
 * 相当于一个工程师连续工作一个月
 *
 * 这也出不来太多的东西。。。。。。
 *
 * 要明白是为了教你架构的能力，这个支撑高并发的缓存架构，不是在说怎么做一个很复杂的项目，库存系统，商品详情页
 *
 * 系统。拿我参与过的真实的项目作为背景，浓缩和简化了业务之后在业务背景下去学习架构的知识，可以理论结合实际，在
 *
 * 业务中去学习，效果肯定非常好的。
 *
 * @author WangCheng
 * @describe
 * @create 2020-05-02 10:17
 */
@Slf4j
public class ProductInventoryDBUpdateRequest implements Request{

    /**
     * 商品库存
     */
    private ProductInventory productInventory;

    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;

    public ProductInventoryDBUpdateRequest(ProductInventory productInventory
            , ProductInventoryService productInventoryService){
        this.productInventory = productInventory;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public void process() {
        log.info("===================日志===================: 数据库更新请求开始执行,商品id=" + productInventory.getProductId() + ",商品库存数量=" + productInventory.getInventoryCnt());
        // 删除redis中的缓存
        productInventoryService.removeProductInventoryCache(productInventory);
        log.info("===================日志===================: 已删除redis中的缓存");
        // 为了演示先删除了redis中的缓存,然后还没更新数据库的时候,读请求过来了,这里可以人工sleep一下
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 修改数据库中的缓存
        productInventoryService.updateProductInventory(productInventory);
    }

    /**
     * 获取商品Id
     * @return
     */
    @Override
    public Integer getProductId() {
        return productInventory.getProductId();
    }

    /**
     * 不要进行强制刷新
     * @return
     */
    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
