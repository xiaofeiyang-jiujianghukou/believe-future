# Believe

一套从零构建的通用型微服务架构，基于 JDK 25 + Spring Boot 4.0.6。

## 缘起

微服务架构是当代后端开发的主流范式，但市面上的脚手架要么过于简陋，要么过度封装。Believe 的目标是走一遍完整的构建之路——从零搭建基础框架、实现核心能力、到容器化部署与 CI/CD 交付，深度掌握 SPI、Spring Boot 自动配置等底层机制，最终沉淀出一套可复用的生产级基础设施。

## 技术栈

| 分类 | 组件 |
|------|------|
| 核心 | JDK 25, Spring Boot 4.0.6, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0.0 |
| 注册/配置 | Nacos 3.1.1 |
| 网关 | Spring Cloud Gateway 4.2.x |
| 限流熔断 | Sentinel 1.8.9 |
| 认证授权 | Sa-Token 1.45.0 |
| 数据持久 | MyBatis-Plus 3.5.15, MySQL, HikariCP |
| 缓存 | Redis 7.4.x |
| 任务调度 | xxl-job 2.5.0 |
| 链路追踪 | SkyWalking 9.7.x |
| 分布式事务 | Seata 2.5.0 |
| 消息队列 | RocketMQ 5.3.1 |
| 容器化 | Docker 27.x, Kubernetes 1.32+, GitLab CI |

## 架构

```
应用服务层          believe-gateway    believe-auth    believe-user
                    (8080)             (8101)          (8201)
                         │                  │               │
                         └──────────────────┼───────────────┘
                                            │
基础框架层          ┌───────────────────────┴───────────────────────┐
(believe-framework) │  core  redis  log  feign  data  swagger       │
                    │  mq    security  exception  spi  job          │
                    └───────────────────────────────────────────────┘
```

- **believe-framework** — 父 POM，统一管理版本依赖；内含 10 个 common 子模块 + 1 个 job starter
- **believe-gateway** — API 网关，路由转发、认证鉴权、限流熔断
- **believe-auth** — 认证中心，登录、Token 颁发、RBAC 权限管理
- **believe-user** — 用户中心

## 演进

| 版本 | 里程碑 |
|------|--------|
| v1.0 | 基于 JDK 17 + Spring Boot 3.2.x 起步 |
| v2.0 | 全面升级至 JDK 25 + Spring Boot 4.0.6 |
| v2.1 | 重构项目结构，believe-future 为根节点 |
| v2.2 | 移除根父 POM，版本依赖由 believe-framework 统一管理 |
| v2.3 | 修复 Sa-Token、MyBatis-Plus 的 Spring Boot 4 兼容性问题 |
| v2.5 | 精简版本管理，删除 BOM 已管理的冗余声明 |
| v2.6 | 修正 Knife4j 版本为 4.5.0；AOP 依赖改用 spring-aop + aspectjweaver |

## 快速开始

```bash
# 编译全部模块
cd believe-framework && mvn clean install -DskipTests

# 启动 Nacos（依赖基础设施）
docker compose up -d

# 启动服务
cd ../believe-gateway && mvn spring-boot:run
cd ../believe-auth    && mvn spring-boot:run
cd ../believe-user    && mvn spring-boot:run
```

## 目录结构

```
believe-future/
├── believe-framework/          # 基础框架（版本管理与通用能力）
│   ├── believe-common/         # 10 个通用子模块
│   └── believe-job/            # xxl-job 执行器封装
├── believe-gateway/            # API 网关
├── believe-auth/               # 认证授权中心
├── believe-user/               # 用户中心
├── k8s/                        # Kubernetes 部署文件
├── scripts/                    # 运维脚本
└── docs/                       # 架构文档
```
