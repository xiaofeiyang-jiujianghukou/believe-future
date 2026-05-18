# believe-auth

认证授权中心，提供用户认证、JWT Token 颁发、RBAC 权限管理。基于 Sa-Token + MyBatis-Plus，5 表模型完整实现。

## 适用场景

- 微服务统一认证入口
- 用户注册/登录/登出
- RBAC 角色权限控制
- Token 校验（与 Gateway AuthGlobalFilter 配合）

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-auth</artifactId>
</dependency>
```

### 数据库初始化

执行 `src/main/resources/db/init.sql` 创建 5 张表并初始化管理员角色和权限。

### 配置

```yaml
server:
  port: 8101

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/believe
    username: root
    password: root

sa-token:
  token-name: Authorization
  timeout: 7200
  activity-timeout: 1800
  is-concurrent: true
  jwt-secret-key: ${JWT_SECRET:your-secret-key}
```

## API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/auth/login` | 用户名密码登录 | 否 |
| POST | `/auth/register` | 注册新用户 | 否 |
| POST | `/auth/logout` | 登出 | 是 |
| GET | `/auth/user-info` | 获取当前用户信息和权限 | 是 |

### 登录

```http
POST /auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "123456"
}
```

响应：
```json
{
    "code": 200,
    "data": {
        "token": "xxxxxxxxxx",
        "userId": 1,
        "username": "admin",
        "nickname": "管理员"
    }
}
```

### 获取用户信息

```http
GET /auth/user-info
Authorization: xxxxxxxxxx
```

响应包含角色和权限列表：
```json
{
    "code": 200,
    "data": {
        "userId": 1,
        "username": "admin",
        "roles": ["ROLE_ADMIN"],
        "permissions": ["user:manage", "role:manage", "perm:manage"]
    }
}
```

## 数据模型

5 表 RBAC 模型：

| 表 | 说明 | 核心字段 |
|---|---|---|
| `sys_user` | 用户 | username, password(salt:hash), status |
| `sys_role` | 角色 | name, code(ROLE_ADMIN/ROLE_USER) |
| `sys_permission` | 权限 | name, code(user:manage), type(菜单/按钮/API) |
| `sys_user_role` | 用户-角色 | user_id, role_id |
| `sys_role_permission` | 角色-权限 | role_id, permission_id |

## 权限实现

Sa-Token `StpInterfaceImpl` 自动从数据库加载角色和权限，Gateway `AuthGlobalFilter` 调用 `StpUtil.checkLogin()` 即可完成认证，无需在每个服务中重复鉴权逻辑。

## 启动

```bash
mvn spring-boot:run
```

服务注册到 Nacos 为 `believe-auth`，主端口 8101，管理端口 8102。
