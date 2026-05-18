# believe-user

用户中心服务，提供用户资料管理、分页查询、信息维护等能力。基于 MyBatis-Plus + Sa-Token 权限控制。

## 适用场景

- 用户个人资料查看与更新
- 管理员用户列表分页查询
- 配合 believe-auth 完成认证后的用户数据管理

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-user</artifactId>
</dependency>
```

### 配置

```yaml
server:
  port: 8201

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/believe
    username: root
    password: root

sa-token:
  token-name: Authorization
  timeout: 7200
  jwt-secret-key: ${JWT_SECRET:your-secret-key}
```

## API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/user/info` | 获取当前登录用户信息 | 登录即可 |
| PUT | `/user/info` | 更新当前用户资料 | 登录即可 |
| GET | `/user/{id}` | 查询指定用户 | `user:manage` |
| GET | `/user/list` | 分页查询用户 | `user:manage` |
| DELETE | `/user/{id}` | 删除用户 | `user:manage` |

### 查当前用户

```http
GET /user/info
Authorization: xxxxxxxxxx
```

响应：
```json
{
    "code": 200,
    "data": {
        "id": 1,
        "username": "admin",
        "nickname": "管理员",
        "email": "admin@believe.com",
        "phone": "13800138000",
        "avatar": "https://example.com/avatar.png",
        "status": 1
    }
}
```

### 更新资料

```http
PUT /user/info
Authorization: xxxxxxxxxx
Content-Type: application/json

{
    "nickname": "新昵称",
    "email": "new@believe.com",
    "phone": "13900139000"
}
```

### 管理员分页查询

```http
GET /user/list?pageNum=1&pageSize=10&keyword=admin
Authorization: xxxxxxxxxx
```

响应：
```json
{
    "code": 200,
    "data": {
        "total": 1,
        "pageNum": 1,
        "pageSize": 10,
        "records": [...]
    }
}
```

## 权限说明

- `/user/info`（读写自己）：登录即可，无额外权限要求
- `/user/{id}`、`/user/list`、`DELETE /user/{id}`：需要 `user:manage` 权限，由 auth 模块在 `sys_permission` 表中配置

## 启动

```bash
mvn spring-boot:run
```

服务注册到 Nacos 为 `believe-user`，主端口 8201，管理端口 8202。
