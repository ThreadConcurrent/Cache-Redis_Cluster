package com.roncoo.eshop.inventory.controller;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import com.roncoo.eshop.inventory.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 商品库存Controller
 * @author WangCheng
 * @describe
 * @create 2020-05-02 14:40
 *
 * 大家考虑一次需要模拟的场景:
 *
 * (1)、一个更新商品请求过来,此时会先删除redis中的缓存,然后模拟卡顿5s钟,在卡顿的5s中内,我们发送一个商品
 * (2)、缓存的读请求,因为此时redis中没有缓存,就会来请求将数据库中最新的数据刷新到缓存中
 * (3)、此时读请求会路由到同一个内存队列中阻塞住,不会执行
 * (4)、等5s钟写请求完成了数据库的更新之后读请求才会执行,读请求执行的时候会将最新的库存从数据库中查询出来
 * 然后更新到缓存中
 *
 * 如果是不一致的情况,可能会出现redis中还是库存为100,但是数据库中已经更新库存成99了,
 *
 * 现在做了一致性保障的方法之后,就可以保证说数据是一致的
 *
 * 最后说,包括在这个方案以内,还有后面的各种解决方案,首先都是针对我自己遇到过的特殊场景去设计的
 *
 * 可能这个方案就不一定完全100%适合你的场景,也许还要做一些改造才可以,也有一种可能说方案比较复杂,
 *
 * 即使我做过,有少数细节我疏忽了,没有在具体去说,导致这个方案有一些漏洞和bug都是正常的
 *
 * 吸收了、嚼烂了、改造了才能应用到我们自己的场景中
 *
 * 主要还是架构和设计思,技术思想,一种设计思想,多种设计思想组合起来都是架构思想
 */
@Controller
@Slf4j
public class ProductInventoryController {

    @Resource
    private RequestAsyncProcessService requestAsyncProcessService;

    @Resource
    private ProductInventoryService productInventoryService;

    /**
     * 更新商品库存
     */
    @RequestMapping("/updateProductInventory")
    @ResponseBody
    public Response updateProductInventory(ProductInventory productInventory){
        log.info("===================日志===================: 接收到商品更新库存的请求,商品id=" + productInventory.getProductId() + ",商品库存数量="+productInventory.getInventoryCnt());
        Response response = null;
        try {
            Request request = new ProductInventoryDBUpdateRequest(productInventory,
                    productInventoryService);
            requestAsyncProcessService.process(request);
            response = new Response(Response.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            response = new Response(Response.FAILURE);
        }
        return response;
    }

    /**
     * 获取商品库存
     */
    @RequestMapping("/getProductInventory")
    @ResponseBody
    public ProductInventory getProductInventory(Integer productId){
        log.info("===================日志===================: 接收到一个商品库存的读请求,商品Id=" + productId);
        ProductInventory productInventory = null;
        try {
            // 需要读请求去重的,不要强制去刷新
            Request request = new ProductInventoryCacheRefreshRequest(productId,productInventoryService,false);
            requestAsyncProcessService.process(request);
            // 将请求扔给Service异步去处理之后,就需要while(true)一会儿,在这里hang住
            // 去尝试等待前面有商品库存更新的操作,同时那个缓存刷新的操作,将最新的数据刷新到缓存中
            long startTime = System.currentTimeMillis();
//            log.info("===================日志===================: startTime=" + startTime);
            long waitTime = 0L;
            long endTime = 0L;
            // 等待超过了200ms没有从缓存中获取到结果
            while(true){
//                log.info("===================日志===================: waitTime=" + waitTime);
                // 如果等待的时间超过200ms,直接返回,一般公司里面,面向用户的读请求控制在200ms以内就可以了
                if(waitTime > 25000){
                    break;
                }
                // 尝试去redis中去读取一次商品库存的缓存数据
                productInventory = productInventoryService.getProductInventoryCache(productId);
                // 如果读取到了结果,那么就返回
                if(productInventory != null){
                    log.info("===================日志===================: 在200ms读取到了redis中的库存缓存,商品id=" + productInventory.getProductId() + ",商品库存数量=" + productInventory.getInventoryCnt());
                    return productInventory;
                }
                // 如果没有读取到结果,那么就等待一段时间
                else{
                    Thread.sleep(20);
                    endTime = System.currentTimeMillis();
//                    log.info("===================日志===================: endTime=" + endTime);
                    waitTime = endTime - startTime;
                }
            }
            // 尝试从数据库中读取数据
            productInventory = productInventoryService.findProductInventory(productId);
            if(productInventory != null){
                // 将缓存刷新一下
                // 这个过程实际上是一个读操作的过程,但是没有放在队列中串行去处理,还是有数据不一致的问题
                request = new ProductInventoryCacheRefreshRequest(productId,productInventoryService,true);
                requestAsyncProcessService.process(request);
//                productInventoryService.setProductInventoryCache(productInventory);
                // 代码会运行在这里,只有三种情况
                // 1.就是说,上一次也是读请求,数据刷入了redis,但是redis LRU给清理掉了,标志位还是fasle
                // 所以此时下一个读请求是从缓存中拿不到数据的,那么就会hang一会儿,自动从数据库中去拿,
                // 然后放一个读进request队列,让数据去刷新一下
                // 2.就是说可能在200ms内就是读请求在队列中一直挤压着,没有等待到它执行(在实际生产环境中,基本比较坑了,扩容机器了,优化数据库查询性能)
                // 所以就直接查一次库,给队列中塞进去刷新缓存的请求,但这个请求不会执行,被去重操作给去重掉
                // 3.数据库中本身就没有数据,涉及到缓存穿透的问题,每次都穿透redis到达MySQL数据库
                // 所以在这里可以直接查库将数据返回,后面对缓存穿透特殊处理
                return productInventory;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // 从缓存和数据库都读不到就返回-1
        return new ProductInventory(productId,-1L);
    }


}
