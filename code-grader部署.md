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
  java -Dfile.encoding=UTF-8 -Xms1024m -Xmx2048m -XX:+UseG1GC -XX:-UseCompressedClassPointers -jar app.jar

docker run -d \
  --name code-grader-mysql \
  --network code-grader-network \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=你的密码 \
  -v code-grader-mysql-data:/var/lib/mysql \
  mysql:9.7.0

docker run -d \
  --name code-grader-redis \
  --network code-grader-network \
  -v code-grader-redis-data:/data \
  -p 6379:6379 \
  redis:8.6.0 \
  redis-server --requirepass "你的密码"

docker run -d \
  --name code-grader-nginx \
  --network code-grader-network \
  -p 80:80 \
  -v /opt/code-grader/html:/usr/share/nginx/html:ro \
  -v /opt/code-grader/nginx/nginx.conf:/etc/nginx/nginx.conf:ro \
  nginx:1.31.0


