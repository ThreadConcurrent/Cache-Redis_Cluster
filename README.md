# Cache-Redis_Cluster
搭建缓存架构并测试

初始化docker
systemctl daemon-reload
systemctl restart docker.service
==========================================
执行脚本创建7001-7006并启动
for port in $(seq 7001 7006); \
do \
mkdir -p /mydata/redis/node-${port}/conf
touch /mydata/redis/node-${port}/conf/redis.conf
cat <<EOF>>/mydata/redis/node-${port}/conf/redis.conf
port ${port}
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
cluster-announce-ip 192.168.121.150
cluster-announce-port ${port}
cluster-announce-bus-port 1${port}
appendonly yes
EOF
docker run -p ${port}:${port} -p 1${port}:1${port} --name redis-${port} \
      -v /mydata/redis/node-${port}/data:/data \
      -v /mydata/redis/node-${port}/conf/redis.conf:/etc/redis/redis.conf \
      -d redis:5.0.7 redis-server /etc/redis/redis.conf; \
done
========================================================
进入一台redis实例
docker exec -it redis-7001 /bin/bash

创建集群,前提是cluster版本必须是5.0+,否则使用ruby进行创建
redis-cli --cluster create 
192.168.121.150:7001 192.168.121.150:7002 192.168.121.150:7003 
192.168.121.150:7004 192.168.121.150:7005 192.168.121.150:7006 --cluster-replicas 1

启动redis
docker start redis-7001
docker start redis-7002
docker start redis-7003
docker start redis-7004
docker start redis-7005
docker start redis-7006

集群操作
redis-cli -c -h 192.168.121.150 -p 7001