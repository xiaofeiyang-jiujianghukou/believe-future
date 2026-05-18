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

`spring.config.import` 和 Nacos 地址写入 profile 文件，`spring.cloud.bootstrap.enabled` 不再需要。Docker Compose 通过 `SPRING_PROFILES_ACTIVE=pro` 激活部署配置。   - 或 JVM 参数：`-Dspring.cloud.bootstrap.enabled=true`

关键点：
- `spring.config.import` 写在 `bootstrap.yml`，Nacos server-addr 和 application.name 在此阶段已加载
- 必须显式启用 bootstrap，Spring Cloud 2025.x 默认 `spring.cloud.bootstrap.enabled=false`
- `optional:` 前缀确保 Nacos 中不存在对应配置时不会启动失败
