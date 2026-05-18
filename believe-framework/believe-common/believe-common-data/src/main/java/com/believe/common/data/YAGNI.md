## YAGNI 原则

**YAGNI** 是 "**You Aren't Gonna Need It**" 的缩写，中文意为 **“你不需要它”**。

这是敏捷开发和极限编程（XP）中的核心原则之一。

---

## 核心理念

> **只实现当前确实需要的功能，不要提前实现你认为“将来可能会用到”的功能。**

---

## 通俗理解

| 场景 | YAGNI 的做法 | 反模式 |
|------|-------------|--------|
| 写代码时 | 只写今天需要的代码 | 写“以后可能会用到”的代码 |
| 加功能时 | 等到真正需要时再加 | 提前加一个“备用”功能 |
| 设计架构 | 当前够用即可 | 过度设计，考虑各种“可能性” |

---

## 在项目中的体现

以你的 `believe-common-data` 为例：

### ❌ 违反 YAGNI（过度设计）

```java
// 提前实现读写分离、多数据源、分库分表...
@Configuration
public class DataSourceConfig {
    @Bean public DataSource masterDataSource() { ... }
    @Bean public DataSource slaveDataSource() { ... }
    @Bean public DataSource routingDataSource() { ... }
    @Bean public DataSource backupDataSource() { ... }
}
```

**问题**：你目前只有单机单库，这些代码写了也永远不会被执行，却增加了复杂度和维护成本。

### ✅ 符合 YAGNI（简洁实用）

```java
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        // 单数据源，够用
        return DataSourceBuilder.create().build();
    }
}
```

**优点**：简单、够用、易于理解。

---

## 常见误区

| 说法 | YAGNI 的看法 |
|------|-------------|
| “以后可能会用到，先写上” | ❌ 浪费时间和精力，可能永远用不到 |
| “这是为了扩展性” | ⚠️ 适度设计可以，过度就是 YAGNI 的反面 |
| “框架/规范要求这样写” | ✅ 那是必须的，不是 YAGNI 反对的 |

---

## 什么时候需要打破 YAGNI？

当你**明确知道**某个功能一定会用到时，可以提前准备：

| 情况 | 是否提前做 |
|------|-----------|
| 明天就要上线的新功能 | ✅ 可以做 |
| 产品经理明确说下个月要做 | ⚠️ 可以设计接口，不实现细节 |
| “我觉得以后可能会需要” | ❌ 不要做 |

---

## 与你项目的关系

你在设计 `believe-common-data` 时：

- ✅ **应该做**：MyBatis-Plus 集成、自动填充、分页插件（现在就需要）
- ❌ **不应该做**：读写分离、多数据源、分库分表（目前用不到）

等你将来真的需要读写分离时，再扩展 `believe-common-data` 也完全来得及。

---

## 总结

> **YAGNI = 别想太多，做好现在。**

它不是一个教条，而是一个提醒：避免在代码里堆砌“可能有用”的功能，保持代码简单、清晰、聚焦当前需求。