# believe-common-swagger

API 文档自动配置模块，集成 Knife4j，提供统一的 Swagger 文档元信息管理。

## 适用场景

- 微服务 API 文档自动生成
- 网关聚合文档的统一标题/版本/联系方式配置

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-swagger</artifactId>
</dependency>
```

> 模块依赖 Knife4j，当 Knife4j 在 classpath 中时自动激活。

### 使用示例

**1. 默认配置（零配置启动）**

引入依赖后即可访问 API 文档，默认元信息：
- 标题：`Believe API`
- 版本：`1.0.0`

**2. 自定义文档信息**

```yaml
believe:
  swagger:
    title: 用户中心 API
    description: 提供用户注册、登录、信息管理等功能
    version: 2.0.0
    contact-name: Believe 用户组
    contact-url: https://believe.com/team
    contact-email: user-team@believe.com
    external-url: https://believe.com/docs/user
    external-description: 用户中心完整文档
```

**3. 访问文档**

```
# 网关聚合文档
http://localhost:8080/doc.html

# 单个服务文档
http://localhost:8201/doc.html
```

## 配置属性

`believe.swagger.*`：

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `title` | `Believe API` | 文档标题 |
| `description` | `Believe Framework REST API` | 文档描述 |
| `version` | `1.0.0` | API 版本 |
| `contact-name` | `Believe Team` | 联系人 |
| `contact-url` | `https://believe.com` | 联系链接 |
| `contact-email` | `team@believe.com` | 联系邮箱 |
| `external-url` | `https://believe.com/docs` | 外部文档链接 |
| `external-description` | `Believe Documentation` | 外部文档描述 |
