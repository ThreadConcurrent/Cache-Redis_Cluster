package com.roncoo.eshop.inventory.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求内存队列
 * @author WangCheng
 * @describe
 * @create 2020-05-02 9:53
 */
public class RequestQueue {

    /**
     * 内存队列
     */
    private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();

    /**
     * 表示位Map
     */
    private Map<Integer,Boolean> flagMap = new ConcurrentHashMap<Integer, Boolean>();

    /**
     * 单例有很多种方式去实现,我采用绝对线程安全的一种单例模式
     *
     * 静态内部类的方式,去初始化单例
     */
    private static class Singleton{

        private static RequestQueue instance;

        static{
            instance = new RequestQueue();
        }

        public static RequestQueue getInstance(){
            return instance;
        }
    }

    /**
     * 利用jvm的机制去保证多线程并发安全
     *
     * 内部类的初始化,一定只是会发生一次,不管多少个线程并发去初始化
     * @return
     */
    public static RequestQueue getInstance(){
        return Singleton.getInstance();
    }

    public void addQueue(ArrayBlockingQueue<Request> queue){
        this.queues.add(queue);
    }

    /**
     * 获取内存队列的数量
     * @return
     */
    public int queueSize(){
        return queues.size();
    }

    /**
     * 获取内存队列
     * @param index
     * @return
     */
    public ArrayBlockingQueue<Request> getQueue(int index){
        return queues.get(index);
    }

    public Map<Integer,Boolean> getFlagMap(){
        return flagMap;
    }
}

