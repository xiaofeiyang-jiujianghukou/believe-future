# 问题记录

按时间顺序增量更新，每条记录包含问题原因、复现场景、解决方案。

---

## 1. spring-boot-maven-plugin 未执行 repackage

**时间**：2026-05-19

**问题原因**：

`believe-framework` 父 POM 使用 `spring-boot-dependencies` BOM 管理依赖版本，而非继承 `spring-boot-starter-parent`。`spring-boot-dependencies` 只管 `<dependencyManagement>`，不传递插件版本和默认执行绑定。父 POM 的 `<pluginManagement>` 中声明了 `spring-boot-maven-plugin`，但未配置 `<executions>`，导致 `repackage` 目标不会绑定到 `package` 生命周期。Maven 打包产物为原始 thin JAR（无 Main-Class），Java 无法执行。

**复现场景**：

```bash
mvn package -DskipTests -f believe-auth/pom.xml
# 输出只有 jar:jar (default-jar)，没有 spring-boot:repackage
# 目标 JAR 仅 ~24KB，缺少 BOOT-INF/ 目录和 Main-Class
java -jar believe-auth/target/*.jar
# 报错：no main manifest attribute, in app.jar
```

**解决方案**：

在 `believe-framework/pom.xml` 的 `<pluginManagement>` 中为 `spring-boot-maven-plugin` 显式添加 `<executions>`：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>${spring-boot.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## 2. Spring Cloud 2025.x 要求显式 spring.config.import

**时间**：2026-05-19

**问题原因**：

Spring Cloud 2025.x 废弃了 bootstrap 上下文，Nacos Config 不再自动从 `spring.cloud.nacos.config` 配置中隐式导入。必须通过 `spring.config.import` 显式声明要导入的配置源，否则启动报错 `No spring.config.import property has been defined`。

**复现场景**：

服务启动时直接报错退出：

```
APPLICATION FAILED TO START
***************************
No spring.config.import property has been defined

Action:
Add a spring.config.import=nacos: property to your configuration.
```

**解决方案**：

在 `bootstrap.yml` 中添加 `spring.config.import`，**同时启用 bootstrap 上下文**（Spring Cloud 2025.x 默认关闭）：

1. `bootstrap.yml`：
```yaml
spring:
  config:
    import: optional:nacos:${spring.application.name}.yaml?group=DEFAULT_GROUP
  application:
    name: believe-auth
  cloud:
    nacos:
      ...
```

2. 启用 bootstrap（二选一）：
   - 环境变量：`SPRING_CLOUD_BOOTSTRAP_ENABLED=true`

> 后续优化：完全移除 bootstrap.yml，改用 application-{profile}.yml 分层（见 #3）。

---

## 3. MybatisPlus MetaObjectHandler 重复 Bean

**时间**：2026-05-19

**问题原因**：

`MybatisMetaObjectHandler` 被三个路径同时注册为 `MetaObjectHandler` 类型的 Bean：
1. 类上的 `@Component` 注解 → Spring 组件扫描自动注册
2. `DataAutoConfiguration.CommonDataConfig` 的 `@Import(MybatisMetaObjectHandler.class)` → 显式导入注册
3. `MybatisPlusConfig.metaObjectHandler()` 的 `@Bean` 方法 → 手动创建注册

三者在同一配置类被处理时，`@ConditionalOnMissingBean` 条件判断顺序不确定，导致 MybatisPlus 自动配置检测到多个 `MetaObjectHandler` Bean 而报错。

**复现场景**：

```
Parameter 0 of method sqlSessionTemplate in 
com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration 
required a single bean, but 2 were found:
  - metaObjectHandler: defined by method 'metaObjectHandler' in ...
  - com.believe.common.data.handler.MybatisMetaObjectHandler: defined in unknown location
```

**解决方案**：

只保留 `MybatisPlusConfig.@Bean` 作为 `MetaObjectHandler` 的唯一注册入口：

1. 移除 `MybatisMetaObjectHandler` 上的 `@Component`
2. 从 `DataAutoConfiguration` 的 `@Import` 中移除 `MybatisMetaObjectHandler.class`
3. 保留 `MybatisPlusConfig.metaObjectHandler()` 的 `@Bean` + `@ConditionalOnMissingBean` + `@ConditionalOnProperty`

## 4. 移除 bootstrap.yml，改用 application-{profile}.yml

**时间**：2026-05-19

**问题原因**：

Spring Cloud 2025.x 默认关闭 bootstrap 上下文，且 Nacos 地址等配置放在 `bootstrap.yml` 导致与 `spring.config.import` 之间存在鸡生蛋的依赖问题。改用 profile 分层后，本地开发与 Docker 部署使用不同的配置文件，配置归属更清晰。

**解决方案**：

每个服务拆分为三层配置：

```
application.yml           ← 公共配置（端口、datasource、mybatis-plus、sa-token、日志等）
application-dev.yml       ← 本地开发（localhost Nacos）
application-pro.yml       ← Docker 部署（容器名 Nacos）
```

`spring.config.import` 和 Nacos 地址写入 profile 文件，`spring.cloud.bootstrap.enabled` 不再需要。Docker Compose 通过 `SPRING_PROFILES_ACTIVE=pro` 激活部署配置。

关键点：
- `spring.config.import` 写在 `bootstrap.yml`，Nacos server-addr 和 application.name 在此阶段已加载
- 必须显式启用 bootstrap，Spring Cloud 2025.x 默认 `spring.cloud.bootstrap.enabled=false`
- `optional:` 前缀确保 Nacos 中不存在对应配置时不会启动失败

---

## 5. believe-gateway 启动报 WebServerInitializedEvent ClassNotFoundException

**时间**：2026-05-19

**问题原因**：

Spring Boot 4.x 对核心 jar 做了拆分，`WebServerInitializedEvent` 从 `spring-boot` jar 移到了 `spring-boot-web-server` jar。`spring-cloud-gateway-server-webflux:5.0.0` 将 `spring-boot-starter-webflux` 声明为 `<optional>true</optional>`，不再传递引入。缺少 `spring-boot-web-server` 导致 `AbstractAutoServiceRegistration` 在 bean 后处理阶段无法解析 `WebServerInitializedEvent` 参数类型，启动直接崩溃。

依赖链：`spring-boot-starter-webflux` → `spring-boot-starter-reactor-netty` → `spring-boot-reactor-netty` → `spring-boot-web-server`（含 `WebServerInitializedEvent`）

**复现场景**：

```bash
mvn spring-boot:run -f believe-gateway/pom.xml
```

```
Caused by: java.lang.ClassNotFoundException:
  org.springframework.boot.web.server.context.WebServerInitializedEvent

Error creating bean with name 'nacosAutoServiceRegistration':
  Failed to introspect AbstractAutoServiceRegistration
```

**解决方案**：

显式引入 `spring-boot-starter-webflux`：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

验证：`spring-boot-web-server-4.0.6.jar` 出现在依赖树中，`WebServerInitializedEvent` 可正常加载。

---

## 6. believe-common-log 强依赖 spring-boot-starter-web 污染 WebFlux 项目

**时间**：2026-05-19

**问题原因**：

`believe-common-log` 中 `TraceIdFilter` 继承 `OncePerRequestFilter`（Servlet 栈），模块 POM 将 `spring-boot-starter-web` 声明为 compile scope。所有引用该模块的项目（包括 WebFlux 网关）都会被传递引入完整的 Servlet/MVC 栈。MVC 与 WebFlux 共存可能引发自动配置冲突，且增加不必要的依赖体积。

Gateway 有自己的 `TraceIdGlobalFilter`（WebFilter 实现），不需要 Servlet 版 `TraceIdFilter`。

**解决方案**：

两步联动修改，WebMVC 项目自动启用 TraceIdFilter，WebFlux 项目自动跳过：

1. `believe-common-log/pom.xml` — scope 改为 `provided`，不再传递：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <scope>provided</scope>
</dependency>
```

2. `LogAutoConfiguration.java` — 加条件注解，WebFlux 环境（无 `OncePerRequestFilter`）自动跳过：
```java
@AutoConfiguration
@Import(LogAspect.class)
public class LogAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(OncePerRequestFilter.class)
    @Import(TraceIdFilter.class)
    static class TraceIdFilterConfiguration {
    }
}
```

---

## 7. Gateway 限流/认证配置无法动态刷新

**时间**：2026-05-19

**问题原因**：

`SlidingWindowRateLimiter` 和 `SentinelRateLimiter` 的 `enabled`/`qps` 通过 `@Value` 注入构造函数并存入 `final` 字段；`AuthGlobalFilter` 的白名单同样如此。Bean 创建后这些值不再变化，Nacos 推送新配置后也必须重启才能生效。

**复现场景**：

1. 启动 gateway，qps 配置为 100
2. Nacos 中将 `believe.gateway.rate-limit.qps` 改为 50
3. 发送请求，仍在 100 QPS 处限流，新值未生效
4. 同理，修改 `believe.gateway.auth.whitelist` 后新增的白名单路径仍需认证

**解决方案**：

统一采用 `@ConfigurationProperties` + `@RefreshScope` 模式：

1. 新建 `RateLimitProperties`，Nacos 推送时自动刷新绑定的属性：
```java
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "believe.gateway.rate-limit")
public class RateLimitProperties {
    private boolean enabled = false;
    private int qps = 100;
    private String type = "sliding-window";
}
```

2. `SlidingWindowRateLimiter` 去掉 `final` 字段，每次请求实时读取：
```java
// 前：构造函数注入 final int qps，永不变化
// 后：注入 RateLimitProperties，rateLimit() 中实时 properties.getQps()
```

3. `SentinelRateLimiter` 除实时读取外，监听 `RefreshScopeRefreshedEvent` 自动更新 `FlowRule`：
```java
@EventListener(RefreshScopeRefreshedEvent.class)
public void onRefresh() {
    loadRules(properties.getQps());
}
```

4. `AuthGlobalFilter` 加 `@RefreshScope`，白名单变更时 Bean 自动重建。

效果：修改 Nacos 上的 QPS、开关、白名单等配置，无需重启网关，数秒内生效。
