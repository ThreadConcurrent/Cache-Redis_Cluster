package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池:单例
 * @author WangCheng
 * @describe
 * @create 2020-05-02 8:35
 */
public class RequestProcessorThreadPool {

    // 在实际的项目中,你设置的线程池大小是多少,每个线程监控的那个内存队列的大小是多少
    // 我们都可以做到一个外部的配置文件中
    // 我这里给简化了,写死了
    /**
     * 线程池
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    /**
     *
     */
    public RequestProcessorThreadPool(){
        // 初始化的时候创建10个内存队列
        RequestQueue requestQueue = RequestQueue.getInstance();
        for(int i = 1;i <= 10;i++){
            ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<Request>(100);
            // 然后将queue添加到requestQueue(外层对内存队列的一个封装)
            requestQueue.addQueue(queue);
            // 然后将这个queue和thread绑定在一起,同时将这个thread扔到线程池中
            // 扔到线程池中就不断的where(true)去做一个后台的线程去工作
            threadPool.submit(new RequestProcessorThread(queue));
        }
    }

    /**
     * 单例有很多种方式去实现,我采用绝对线程安全的一种单例模式
     *
     * 静态内部类的方式,去初始化单例
     */
    private static class Singleton{

        private static RequestProcessorThreadPool instance;

        static{
            instance = new RequestProcessorThreadPool();
        }

        public static RequestProcessorThreadPool getInstance(){
            return instance;
        }
    }

    /**
     * 利用jvm的机制去保证多线程并发安全
     *
     * 内部类的初始化,一定只是会发生一次,不管多少个线程并发去初始化
     * @return
     */
    public static RequestProcessorThreadPool getInstance(){
        return Singleton.getInstance();
    }

    /**
     * 初始化的便捷方法
     */
    public static void init(){
        getInstance();
    }
}

