package com.roncoo.eshop.inventory;

import com.roncoo.eshop.inventory.listener.InitListener;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author WangCheng
 * @describe
 * @create 2020-04-27 15:48
 */
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@MapperScan("com.roncoo.eshop.inventory.mapper")
public class InventoryMain {

    /**
     * 加载数据源
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource() {
        return new DataSource();
    }

    /**
     * 读取MyBatis配置文件
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 基于不同的持久层加载不同的事务平台管理器
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * 连接Redis Cluster
     * @return
     */
    @Bean
    public JedisCluster JedisClusterFactory() {
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            jedisClusterNodes.add(new HostAndPort("192.168.121.150",7001));
            jedisClusterNodes.add(new HostAndPort("192.168.121.150",7002));
            jedisClusterNodes.add(new HostAndPort("192.168.121.150",7003));
            JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
            return jedisCluster;
    }

    /**
     * 注册监听器
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean(){
        ServletListenerRegistrationBean servletListenerRegistrationBean =
                new ServletListenerRegistrationBean();
        servletListenerRegistrationBean.setListener(new InitListener());
        return servletListenerRegistrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(InventoryMain.class, args);
    }
}
