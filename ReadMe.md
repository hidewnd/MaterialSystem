# 基于微服务的物料管理系统

——2022毕业设计整理

**基于 vue-next-admin/Element UI 和 Spring Boot/Spring Cloud & Alibaba 前后端分离的分布式微服务架构**

## 项目介绍

本项目是旨在基于当下自身的技术栈开发的一套微服务框架，并遵循高内聚、低耦合原则将系统各模块进行拆分。并在此基础上实现的业务服务

- 采用前后端分离模式，微服务前端采用开源项目[vue-next-admin](https://gitee.com/lyt-top/vue-next-admin.git) 
- 后端采用Spring Boot、Spring Cloud & Alibaba。
- 注册中心、配置中心选型Nacos，权限认证采用Redis+Oauth2.0+JWT方案。
- 业务模块基于领域驱动设计实现解耦，提高扩展性



### 相关技术栈

| 技术                     | 版本          |
|------------------------| ------------- |
| SpringBoot             | 2.4.2         |
| Spring Cloud & Netflix | 2020.0.4      |
| Spring Cloud & Alibaba | 2021.1        |
| Redis                  |               |
| MongoDB                |               |
| MybatisPlus            | 3.4.3         |
| RabbitMQ               |               |
| GatgeWay               |               |
| Sentinel               |               |
| POI/EasyExcel          | 2.2.10        |
| OpenFeign              | 3.0.3         |
| OAtuh2                 | 2.2.4.RELEASE |
| jos.jwt                | 8.16          |
| Vue                    | 3.0           |
| Nginx                  |               |
| ElementUI              |               |
| Docker                 |               |
| SM2                    |               |
| Android SDK10          |               |


## 系统模块



```bash
com.lyne     
├── material-android          							// android端
├── material-api             							// 接口模块
│       └── material-api-system                         // 系统接口
│       └── material-api-material                       // 物料接口
├── material-auth           						 	// 认证中心 [7010]
├── ruoyi-common          								// 通用模块
│       └── ruoyi-common-cache                          // 缓存模块
│              └── ruoyi-common-mongo                   // MongDB缓存模块
│              └── ruoyi-common-redis                   // Redis缓存模块
│       └── ruoyi-common-core                           // 核心模块
│       └── ruoyi-common-log                            // 日志记录
│       └── ruoyi-common-mq                             // 消息队列模块
│       └── ruoyi-common-security                       // 安全模块
│       └── ruoyi-common-web                     		// web服务模块
├── material-gateway         							// 网关模块 [8001]
├── ruoyi-modules        								// 业务模块
│       └── material-module-manufactureManager   		// 生产管理服务 [7060]
│       └── material-module-material                    // 基础物料服务 [7040]
│       └── material-module-materialManager             // 物料管理服务 [7070]
│       └── material-module-system                      // 系统服务 [9300]
│       └── material-module-workflowServer              // 工作流服务 [9300]
├── ruoyi-test          								// 通用测试服务
├── ruoyi-vue          									// 前端框架 [80]
├──pom.xml                // 公共依赖

```



##  架构图

![image-20220905235334483](https://lyne-bucket.oss-cn-shanghai.aliyuncs.com/notes/202209052353630.png)





## 环境要求

- CentOS 7.0+
- Nginx 1.10+
- Node.js 14.19.3
- MySQL 5.7
- Redis 3.0+
- jdk 8.0+





## 演示图









