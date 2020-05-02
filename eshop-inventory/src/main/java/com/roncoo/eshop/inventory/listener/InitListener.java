package com.roncoo.eshop.inventory.listener;

import com.roncoo.eshop.inventory.thread.RequestProcessorThreadPool;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 系统初始化监听器
 * @author WangCheng
 * @describe
 * @create 2020-05-02 8:28
 */
@Slf4j
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        log.info("==============系统初始化===============");
        // 初始化工作线程和内存队列
        RequestProcessorThreadPool.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
