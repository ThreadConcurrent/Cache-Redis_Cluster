package com.roncoo.eshop.inventory.thread;


import com.roncoo.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行请求的工作线程
 * @author WangCheng
 * @describe
 * @create 2020-05-02 9:40
 */
@Slf4j
public class RequestProcessorThread implements Callable<Boolean> {

    /**
     * 自己监控的内存队列
     */
    private ArrayBlockingQueue<Request> queue;

    public RequestProcessorThread(ArrayBlockingQueue<Request> queue){
        this.queue = queue;
    }

    @Override
    public Boolean call() throws Exception {
        // 这里就是说从队列中拿出一条数据以后,如果说是写请求,直接变为true,
        // 如果是读请求先过来,那么直接就是false
        // 前面已经有一个读请求了那么就直接返回,就不执行process的操作了,
        try{
            while(true){
                // ArrayBlockingQueue
                // BlockingQueue就是说嘛,如果队列满了,或者是空的,那么都会在执行操作的时候,阻塞住的
                // 如果这个队列是空的,那么就会在这里阻塞住
                // take到了就会去执行request的操作
                Request request = queue.take();
                log.info("===================日志===================: 工作线程处理请求,商品id=" + request.getProductId());
                boolean forceRfresh = request.isForceRefresh();
                    RequestQueue requestQueue = RequestQueue.getInstance();
                    // false情况下,没有进行强制刷新,那么就是要去去重
                if(!forceRfresh) {
                    // 先做去请求的去重,如果是一个写请求
                    Map<Integer, Boolean> flagMap = requestQueue.getFlagMap();
                    if (request instanceof ProductInventoryDBUpdateRequest) {
                        // 如果是一个更新数据库的请求,那么就将那个productId对应的标识位设置为true
                        flagMap.put(request.getProductId(), true);
                    } else if (request instanceof ProductInventoryCacheRefreshRequest) {
                        // 如果是缓存刷新的请求,那么容器也必须支持多线程并发安全的,那么就判断如果标识不为空
                        // 而且是true,就说明之前有一个商品的数据库更新请求
                        Boolean flag = flagMap.get(request.getProductId());
                        // 如果flag是null
                        // 说明读写请求都没有
                        // 读请求第一次来就把它设置为false,后面还是会吧读请求加载到内存队列中
                        // 完成缓存的刷新的工作
                        // 后面再有都请求过来发现说flag他是false,那么就直接hang一会儿,然后查不到就取数据库查询
                        // 但是,如果说reids内存满了,将这个缓存给删掉了,那么缓存这份数据就永远是null了
                        if (flag == null) {
                            flagMap.put(request.getProductId(), false);
                        }

                        if (flag != null && flag) {
                            flagMap.put(request.getProductId(), false);
                        }

                        //如果是缓存刷新的请求,而且发现那个标识不为空,但是标识是false,
                        //说明前面已经有一个数据库的更新请求+缓存刷新请求,大家想一想
                        if (flag != null && !flag) {
                            // 对于这种读请求,直接就过滤掉,不要放到后面的内存队列里面去了
                            return true;
                        }
                    }
                }
                // 执行request操作
                request.process();

                // 假如说执行完了一个读请求之后,假设这个数据已经刷新到了redis中了,
                // 但是后面可能redis中的数据会因为内存满了被自动清理掉
                // 如果说数据从redis中被自动清理掉了以后,
                // 然后后面又来一个都请求,此时发现标志位是false
                // 其实就不会执行刷新缓存的操作了,
                // 所以在执行完这个都请求之后,实际上这个标志位是停留在false的
                // 在这里那么就会返回
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
