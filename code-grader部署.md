# ==================== 主服务（Spring Boot） ====================

docker run -d \
  --name code-grader-app \
  --network code-grader-network \
  -p 8080:8080 \
  -e LANG=C.UTF-8 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /opt/code-grader/app/cg-server.jar:/app/app.jar \
  -v /opt/code-grader/upload:/app/upload \
  -v /opt/code-grader/output:/app/output \
  -v /opt/code-grader/workspace:/app/code-workspace \
  -w /app \
  graalvm:25.0.3 \
  java -Dfile.encoding=UTF-8 -Xms512m -Xmx1024m -XX:+UseG1GC -XX:-UseCompressedClassPointers -jar app.jar

# ==================== MySQL 数据库 ====================

docker run -d \
  --name code-grader-mysql \
  --network code-grader-network \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=你的密码 \
  -v code-grader-mysql-data:/var/lib/mysql \
  mysql:9.7.0

# ==================== Redis 缓存 ====================

docker run -d \
  --name code-grader-redis \
  --network code-grader-network \
  -v code-grader-redis-data:/data \
  -p 6379:6379 \
  redis:8.6.0 \
  redis-server --requirepass "你的密码"

# ==================== Nginx 前端 ====================

docker run -d \
  --name code-grader-nginx \
  --network code-grader-network \
  -p 80:80 \
  -v /opt/code-grader/html:/usr/share/nginx/html:ro \
  -v /opt/code-grader/nginx/nginx.conf:/etc/nginx/nginx.conf:ro \
  nginx:1.31.2

# ==================== RocketMQ 消息队列 ====================

# RocketMQ NameServer（服务注册中心 — 轻量无状态，路由发现）
docker run -d \
  --name code-grader-rocketmq \
  --network code-grader-network \
  -p 9876:9876 \
  -e TZ=Asia/Shanghai \
  -v code-grader-rocketmq-namesrv-logs:/home/rocketmq/logs \
  rocketmq:5.5.0 \
  sh mqnamesrv

# RocketMQ Broker（消息代理 — 存储和投递消息）
docker run -d \
  --name code-grader-rocketmq-broker \
  --network code-grader-network \
  -p 10911:10911 \
  -p 10909:10909 \
  -e TZ=Asia/Shanghai \
  -e "NAMESRV_ADDR=code-grader-rocketmq:9876" \
  -v code-grader-rocketmq-broker-logs:/home/rocketmq/logs \
  -v code-grader-rocketmq-broker-store:/home/rocketmq/store \
  rocketmq:5.5.0 \
  sh mqbroker -n code-grader-rocketmq:9876

# ==================== 代码沙箱判题服务 ====================

docker run -d \
  --name code-grader-judge \
  --network code-grader-network \
  -p 8081:8081 \
  -e LANG=C.UTF-8 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /opt/code-grader/judge/code-grader-judge.jar:/app/app.jar \
  -v /opt/code-grader/workspace:/app/code-workspace \
  -w /app \
  graalvm:25.0.3 \
  java -Dfile.encoding=UTF-8 -Xms512m -Xmx1024m -XX:+UseG1GC -XX:-UseCompressedClassPointers -jar app.jar --spring.profiles.active=prod

