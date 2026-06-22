# Code Grader 项目文档

## 技术栈

### 核心框架
- **Spring Boot 4.1.0** - Web MVC 框架
- **Java 25** - JDK 版本

### 持久层
- **MyBatis-Plus 3.5.16** - ORM 框架
- **MySQL Connector 9.7.0** - 数据库驱动
- **Redis** - 缓存数据库

### 消息队列
- **RocketMQ 5.5.0** + **rocketmq-spring-boot-starter 2.3.6** - 消息队列，用于主服务与判题服务的异步通信

### 安全认证
- **JWT (jjwt 0.13.0)** - Token 认证
- **jbcrypt 1.0.2** - 密码加密

### API 文档
- **Knife4j 4.5.0** - Swagger UI 增强
- **SpringDoc OpenAPI 3.0.3** - OpenAPI 规范

### 工具库
- **Lombok 1.18.46** - 简化代码
- **Apache Commons Lang3 3.20.0** - 工具类

### 特色功能
- **JPlag 6.3.0** - 代码查重系统
- **Docker Java 3.7.1** - Docker 容器管理
- **Java Parser 3.28.2** - Java 代码解析
- **虚拟线程** - Spring Boot 4 虚拟线程支持

### 构建工具
- **Maven** - 项目构建和依赖管理

---

## 代码规范

### 命名规范
- **变量名**: 使用 camelCase（小驼峰）格式
- **类名**: 使用 PascalCase（大驼峰）格式
- **常量**: 使用 UPPER_SNAKE_CASE 格式
- **包名**: 全部小写，使用点号分隔

### 实体类规范
- 所有实体类实现 `Serializable` 接口
- 使用 `@Data`、`@Builder`、`@NoArgsConstructor`、`@AllArgsConstructor` Lombok 注解
- 使用 `@TableId(type = IdType.AUTO)` 指定主键自增
- 添加 `@Serial private static final long serialVersionUID = 1L;` 字段

### DTO/VO 规范
- **DTO (Data Transfer Object)**: 位于 `cg-pojo/src/main/java/icu/binglieyan/dto/`
  - 用于接收前端参数或服务间消息传递
  - 命名格式：`EntityDTO`、`EntityUpdateDTO`、`EntityPageQueryDTO`
- **VO (View Object)**: 位于 `cg-pojo/src/main/java/icu/binglieyan/vo/`
  - 用于返回前端数据
  - 命名格式：`EntityVO`、`EntityPageQueryVO`

### 统一返回结果
使用 `Result<T>` 封装返回结果：
- 成功（无数据）：`Result.success()`
- 成功（带数据）：`Result.success(data)`
- 错误：`Result.error("错误信息")`

### 异常处理
- 自定义业务异常位于 `cg-common/src/main/java/icu/binglieyan/exception/`
- 异常类命名：`EntityException`（如 `UsersException`、`QuestionsException`）
- 关键操作需实现异常处理和回滚机制
- 全局异常处理：`GlobalExceptionHandler` 位于 `cg-server/handler/`

### 注释规范
- 类、方法需添加 Javadoc 注释
- 注释语言：中文
- 作者标注：`@author binglieyan`

### 对象构建规范
- 优先使用 Builder 方法构建对象
- 使用 Builder 模式时通过 `@Builder` 注解实现

---

## 微服务架构

项目采用微服务拆分，通过 RocketMQ 进行异步通信：

```
┌──────────────┐    JUDGE_REQUEST      ┌─────────────────┐
│  cg-server   │ ────────────────────→ │  code-grader-   │
│  (主服务)     │                       │  judge (判题服务) │
│  :8080       │ ←──────────────────── │  :8081           │
└──────────────┘    JUDGE_RESULT       └─────────────────┘
```

- **cg-server（主服务）**: 负责业务逻辑、API 接口、用户认证。通过 `JudgeRequestProducer` 发送判题请求，通过 `JudgeResultConsumer` 接收判题结果。
- **code-grader-judge（判题服务）**: 独立的代码沙箱判题服务，不在本仓库中。通过 Docker 容器隔离运行学生代码，完成判题后发回结果。

### RocketMQ 消息通信
- **消息主题**:
  - `JUDGE_REQUEST` — 判题请求（cg-server → judge）
  - `JUDGE_RESULT` — 判题结果（judge → cg-server）
- **Producer**: `cg-server/src/main/java/icu/binglieyan/producer/JudgeRequestProducer.java`
- **Consumer**: `cg-server/src/main/java/icu/binglieyan/consumer/JudgeResultConsumer.java`
- **DTO**: `JudgeRequestDTO`（submissionId, assignmentId, studentId）、`JudgeResultDTO`（submissionId, statusCode, totalScore, errorMessage）

---

## 目录结构

```
code-grader/
├── cg-common/                      # 公共模块
│   ├── src/main/
│   │   ├── java/icu/binglieyan/
│   │   │   ├── constant/           # 常量类
│   │   │   │   ├── JwtClaimsConstant.java
│   │   │   │   └── MessageConstant.java
│   │   │   ├── context/            # 上下文类
│   │   │   │   └── BaseContext.java
│   │   │   ├── exception/          # 自定义异常
│   │   │   │   ├── BaseException.java
│   │   │   │   ├── UsersException.java
│   │   │   │   └── ... (其他实体异常)
│   │   │   ├── filters/            # 过滤器
│   │   │   │   └── UserScopeFilter.java
│   │   │   ├── properties/         # 配置属性类
│   │   │   │   └── JwtProperties.java
│   │   │   ├── result/             # 统一返回结果
│   │   │   │   ├── Result.java
│   │   │   │   └── PageResult.java
│   │   │   └── utils/              # 工具类
│   │   │       ├── JwtUtil.java
│   │   │       ├── IpUtil.java
│   │   │       └── UserAgentUtil.java
│   │   └── resources/
│   └── pom.xml
│
├── cg-pojo/                        # POJO 模块（实体、DTO、VO）
│   ├── src/main/java/icu/binglieyan/
│   │   ├── dto/                    # 数据传输对象（含 RocketMQ 消息体）
│   │   │   ├── UsersDTO.java
│   │   │   ├── JudgeRequestDTO.java    # 判题请求消息
│   │   │   ├── JudgeResultDTO.java     # 判题结果消息
│   │   │   └── ... (其他 DTO)
│   │   ├── entity/                 # 实体类
│   │   │   ├── Users.java
│   │   │   ├── Assignments.java
│   │   │   ├── Submissions.java
│   │   │   ├── Questions.java
│   │   │   ├── TestCases.java
│   │   │   ├── TestCaseResults.java
│   │   │   ├── QuestionSubmissions.java
│   │   │   ├── Classes.java
│   │   │   ├── PlagiarismChecks.java
│   │   │   ├── PlagiarismComparisons.java
│   │   │   ├── Department.java
│   │   │   ├── Major.java
│   │   │   ├── DictData.java
│   │   │   ├── DictType.java
│   │   │   └── ... (其他实体)
│   │   └── vo/                     # 视图对象
│   │       ├── UsersVO.java
│   │       ├── AssignmentsVO.java
│   │       ├── QuestionsVO.java
│   │       ├── PlagiarismChecksVO.java
│   │       ├── HiddenTestCaseResultsVO.java
│   │       └── ... (其他 VO)
│   └── pom.xml
│
├── cg-server/                      # 服务模块（核心业务）
│   ├── src/main/
│   │   ├── java/icu/binglieyan/
│   │   │   ├── CgApplication.java  # 启动类
│   │   │   ├── config/             # 配置类
│   │   │   │   ├── WebMvcConfiguration.java
│   │   │   │   ├── MybatisPlusConfiguration.java
│   │   │   │   ├── RedisConfiguration.java
│   │   │   │   ├── Knife4jConfiguration.java
│   │   │   │   └── JacksonConfiguration.java
│   │   │   ├── controller/         # 控制器
│   │   │   │   ├── admin/          # 管理员接口
│   │   │   │   ├── teacher/        # 教师接口
│   │   │   │   └── student/        # 学生接口
│   │   │   ├── producer/           # RocketMQ 生产者
│   │   │   │   └── JudgeRequestProducer.java
│   │   │   ├── consumer/           # RocketMQ 消费者
│   │   │   │   └── JudgeResultConsumer.java
│   │   │   ├── interceptor/        # 拦截器
│   │   │   │   └── JwtAuthenticationUserInterceptor.java
│   │   │   ├── handler/            # 全局异常处理器
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── mapper/             # Mapper 接口
│   │   │   ├── service/            # 服务层
│   │   │   │   └── impl/           # 服务实现类
│   │   └── resources/
│   │       ├── application.yml     # 主配置文件（激活 dev profile）
│   │       ├── application-dev.yml # 开发环境配置
│   │       ├── application-prod.yml# 生产环境配置
│   │       └── mapper/             # MyBatis XML 映射文件
│   └── pom.xml
│
├── sql/                            # 数据库脚本
│   └── code_grader.sql             # 完整建库脚本（含表结构和初始数据）
│
├── ���ginx配置/                       # Nginx 配置
│   └── nginx.conf                  # 前端反向代理配置
│
├── upload/                         # 文件上传目录
├── code-grader部署.md              # Docker 部署文档（微服务全栈部署）
├── .gitignore                      # Git 忽略配置
├── pom.xml                         # 父 POM（依赖管理）
├── mvnw                            # Maven Wrapper
└── mvnw.cmd                        # Maven Wrapper (Windows)
```

---

## 模块说明

### cg-common（公共模块）
- 提供通用工具类、常量、异常、返回结果等
- 包含 JWT 认证相关工具
- 用户上下文管理（BaseContext）
- 过滤器（UserScopeFilter 角色范围过滤）

### cg-pojo（POJO 模块）
- 存放所有实体类、DTO、VO
- DTO 包含业务请求参数和 RocketMQ 消息传递对象
- 不包含业务逻辑
- 被其他模块依赖

### cg-server（服务模块）
- 核心业务逻辑
- RESTful API 接口（admin/teacher/student 三层权限隔离）
- Producer 发送判题请求到 RocketMQ
- Consumer 接收 judge 服务返回的判题结果
- 配置类、拦截器、异常处理器
- MyBatis Mapper 和 Service 层

---

## 配置文件说明

### 配置架构
配置文件采用 Spring Boot 多环境 profile 架构，通过 `cg.*` 自定义前缀统一管理配置占位符：

- **application.yml** — 主配置（激活 `dev` profile），定义配置占位符结构
- **application-dev.yml** — 开发/测试环境具体配置值
- **application-prod.yml** — 生产环境具体配置值

### 核心配置项

```yaml
server.port: 8080                       # 服务端口
spring.threads.virtual.enabled: true    # 启用虚拟线程
spring.servlet.multipart:               # 文件上传：max 10MB

# 数据源（通过 cg.datasource.* 占位符）
cg.datasource.driver-class-name / host / port / database / username / password

# Redis（通过 cg.redis.* 占位符）
cg.redis.host / port / password / database

# RocketMQ（通过 cg.rocketmq.* 占位符）
cg.rocketmq.name-server                # NameServer 地址
cg.rocketmq.producer-group             # 生产者组
cg.rocketmq.consumer-group             # 消费者组
cg.rocketmq.judge-request-topic        # 判题请求主题
cg.rocketmq.judge-result-topic         # 判题结果主题

# JWT
cg.jwt.user-secret-key                 # JWT 签名密钥
cg.jwt.user-ttl: 14400000              # Token 有效期（4小时）

# 文件路径
cg.uploadFile.uploadDir                # 上传文件目录
cg.outputFile.outputDir                # 判题输出目录
```

### MyBatis-Plus 配置
- 更新策略：`not_empty`
- ID 类型：`auto`
- 类型别名包：`icu.binglieyan.dto`, `icu.binglieyan.entity`, `icu.binglieyan.vo`

### 日志级别
- **Mapper**: debug
- **Service**: info
- **Controller**: info

---

## 部署架构

详见 `code-grader部署.md`，整体为 Docker 容器化微服务部署：

| 服务 | 容器名 | 端口 | 镜像 |
|------|--------|------|------|
| 主服务 | code-grader-app | 8080 | GraalVM 25.0.3 |
| 判题服务 | code-grader-judge | 8081 | GraalVM 25.0.3 |
| MySQL | code-grader-mysql | 3306 | MySQL 9.7.0 |
| Redis | code-grader-redis | 6379 | Redis 8.6.0 |
| Nginx | code-grader-nginx | 80 | Nginx 1.31.2 |
| RocketMQ NameServer | code-grader-rocketmq | 9876 | RocketMQ 5.5.0 |
| RocketMQ Broker | code-grader-rocketmq-broker | 10911/10909 | RocketMQ 5.5.0 |

所有服务通过 `code-grader-network` Docker 网络通信。Java 服务使用 GraalVM 25.0.3 运行时，配置 `-Xms512m -Xmx1024m -XX:+UseG1GC -XX:-UseCompressedClassPointers`。

---

## 项目特点

1. **多角色支持**: 管理员、教师、学生三种角色，controller 按角色分包隔离
2. **JWT 认证**: 基于 Token 的身份验证（有效期 4 小时），通过 `JwtAuthenticationUserInterceptor` 拦截校验
3. **微服务判题**: 判题逻辑独立为 `code-grader-judge` 服务，通过 RocketMQ 异步通信解耦
4. **抄袭检测**: 集成 JPlag 代码查重系统
5. **Docker 隔离**: 判题服务使用 Docker 容器隔离运行学生代码
6. **分层架构**: Controller → Service → Mapper 清晰分层
7. **统一响应**: 标准化 API 返回格式（`Result<T>`）
8. **异常处理**: 完善的自定义异常体系 + 全局异常处理器
9. **虚拟线程**: 启用 Java 25 虚拟线程提升并发性能
10. **模块化设计**: cg-common（公共）、cg-pojo（数据对象）、cg-server（服务）三模块 Maven 架构
11. **多环境配置**: profile 驱动的配置管理（dev/prod），`cg.*` 前缀统一管理自定义配置
12. **容器化部署**: 全栈 Docker 部署 + Nginx 反向代理，GraalVM 运行时

---

## 控制器概览

### 管理员接口 (admin/)
- **DepartmentController** - 院系管理
- **DictDataController** - 字典数据管理
- **DictTypeController** - 字典类型管理
- **MajorController** - 专业管理
- **UsersController** - 用户管理

### 教师接口 (teacher/)
- **AssignmentsController** - 作业管理
- **ClassesController** - 班级管理
- **PlagiarismChecksController** - 抄袭检查管理
- **PlagiarismComparisonsController** - 抄袭对比管理
- **QuestionSubmissionsController** - 题目提交管理
- **QuestionsController** - 题目管理
- **SubmissionsController** - 提交管理
- **TestCaseResultsController** - 测试结果管理
- **TestCasesController** - 测试用例管理
- **UsersController** - 用户管理

### 学生接口 (student/)
- **AssignmentsController** - 作业查看
- **ClassesController** - 班级查看
- **QuestionSubmissionsController** - 题目提交
- **QuestionsController** - 题目查看
- **SubmissionsController** - 提交记录
- **TestCaseResultsController** - 测试结果查看
- **TestCasesController** - 测试用例查看
- **UsersController** - 个人信息管理
