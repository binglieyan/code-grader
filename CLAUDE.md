# Code Grader 项目文档

## 技术栈

### 核心框架
- **Spring Boot 4.0.5** - Web MVC 框架
- **Java 25** - JDK 版本

### 持久层
- **MyBatis-Plus 3.5.16** - ORM 框架
- **MySQL Connector 9.7.0** - 数据库驱动
- **Redis** - 缓存数据库

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
- **Java Parser 3.28.1** - Java 代码解析
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
- 添加 `serialVersionUID` 字段

### DTO/VO 规范
- **DTO (Data Transfer Object)**: 位于 `cg-pojo/src/main/java/icu/binglieyan/dto/`
  - 用于接收前端参数
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

### 注释规范
- 类、方法需添加 Javadoc 注释
- 注释语言：中文
- 作者标注：`@author binglieyan`

### 对象构建规范
- 优先使用 `Build` 方法构建对象
- 使用 Builder 模式时通过 `@Builder` 注解实现

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
│   │   ├── dto/                    # 数据传输对象
│   │   │   ├── UsersDTO.java
│   │   │   ├── UsersUpdateDTO.java
│   │   │   ├── QuestionsDTO.java
│   │   │   └── ... (其他 DTO)
│   │   ├── entity/                 # 实体类
│   │   │   ├── Users.java
│   │   │   ├── Questions.java
│   │   │   └── ... (其他实体)
│   │   └── vo/                     # 视图对象
│   │       ├── UsersVO.java
│   │       ├── QuestionsVO.java
│   │       └── ... (其他 VO)
│   └── pom.xml
│
├── cg-server/                      # 服务模块
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
│   │   │   ├── interceptor/        # 拦截器
│   │   │   │   └── JwtAuthenticationUserInterceptor.java
│   │   │   ├── mapper/             # Mapper 接口
│   │   │   ├── service/            # 服务层
│   │   │   │   └── impl/           # 服务实现类
│   │   │   └── handler/            # 处理器
│   │   └── resources/
│   │       ├── application.yml     # 主配置文件
│   │       ├── application-dev.yml # 开发环境配置
│   │       └── mapper/             # MyBatis XML 映射文件
│   └── pom.xml
│
├── upload/                         # 文件上传目录
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
- 用户上下文管理

### cg-pojo（POJO 模块）
- 存放所有实体类、DTO、VO
- 不包含业务逻辑
- 被其他模块依赖

### cg-server（服务模块）
- 核心业务逻辑
- RESTful API 接口
- 配置类、拦截器、过滤器
- MyBatis Mapper 和 Service 层

---

## 配置文件说明

### application.yml
- 服务器端口：8080
- MyBatis-Plus 配置
- SpringDoc/Knife4j 文档配置
- JWT 配置
- Docker 配置
- 文件上传路径配置


### 其他重要配置
- **服务器端口**: 8080
- **虚拟线程**: 已启用 (spring.threads.virtual.enabled: true)
- **文件上传限制**: 最大文件大小 10MB，最大请求大小 10MB
- **MyBatis-Plus**: 更新策略为 not_empty，ID 类型为 auto
- **日志级别**: Mapper(debug), Service(info), Controller(info)

---

## 项目特点

1. **多角色支持**: 管理员、教师、学生三种角色
2. **JWT 认证**: 基于 Token 的身份验证（有效期 4 小时）
3. **代码判题**: 支持自动判题和手动评分
4. **抄袭检测**: 集成 JPlag 代码查重系统
5. **Docker 隔离**: 使用 Docker 容器运行学生代码
6. **分层架构**: Controller → Service → Mapper 清晰分层
7. **统一响应**: 标准化 API 返回格式
8. **异常处理**: 完善的自定义异常体系
9. **虚拟线程**: 启用 Java 25 虚拟线程提升并发性能
10. **模块化设计**: cg-common（公共）、cg-pojo（数据对象）、cg-server（服务）三模块架构

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
