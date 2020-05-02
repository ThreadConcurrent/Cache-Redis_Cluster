package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求异步处理的Service实现
 * @author WangCheng
 * @describe
 * @create 2020-05-02 14:21
 */
@Service("requestAsyncProcessService")
@Slf4j
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {

    @Override
    public void process(Request request) {
        // 这里不能开始就做去重,因为线程是并发执行的
        try {
            // 请求的路由,根据每个请求的商品id,路由到对应的内存队列中去
            ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());
            // 将请求放入到对应的队列中,完成路由操作
            queue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取路由到的内存队列
     * @param productId 商品Id
     * @return 内存队列
     */
    private ArrayBlockingQueue<Request> getRoutingQueue(Integer productId){
        RequestQueue requestQueue = RequestQueue.getInstance();
        // 先获取productId的hash值
        String key = String.valueOf(productId);
        int h;
        int hash = (key == null) ? 0:(h = key.hashCode()) ^ (h >>> 16);
        // 对hash值取模,将hash值路由到指定的内存队列中
        // 比如说内存队列的大小有8个,用内存队列的数量对hash值取模之后结果一定是在0-7之间
        // 所以任何一个商品Id都会被固定路由到同样的内存队列中去的
        int index = RequestQueue.getInstance().queueSize() & hash;
        log.info("===================日志===================: 路由内存队列,商品id=" + productId + ",队列索引=" + index);

        return requestQueue.getQueue(index);
    }
}
