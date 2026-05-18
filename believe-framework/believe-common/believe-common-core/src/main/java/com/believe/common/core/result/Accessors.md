## `@Accessors(chain = true)` 的作用

`@Accessors` 是 Lombok 提供的注解，用于控制生成的 getter/setter 的行为。当设置 `chain = true` 时，**setter 方法会返回当前对象实例（`this`），而不是 `void`**，从而实现**链式调用**。

---

## 对比示例

### 不使用 `@Accessors(chain = true)`

```java
@Data
public class User {
    private String name;
    private Integer age;
}

// 使用时需要分行调用
User user = new User();
user.setName("张三");
user.setAge(25);
```

### 使用 `@Accessors(chain = true)`

```java
@Data
@Accessors(chain = true)
public class User {
    private String name;
    private Integer age;
}

// 可以链式调用
User user = new User()
    .setName("张三")
    .setAge(25);
```

---

## 生成的代码对比

### 默认行为（`chain = false`）

```java
public void setName(String name) {
    this.name = name;
}

public void setAge(Integer age) {
    this.age = age;
}
```

### `chain = true` 时

```java
public User setName(String name) {
    this.name = name;
    return this;  // 返回当前对象
}

public User setAge(Integer age) {
    this.age = age;
    return this;
}
```

---

## 在 Result 和 PageResult 中的应用

### Result 使用示例

```java
// 创建响应并链式设置属性
Resultuser result = new Resultuser()
    .setCode(200)
    .setMessage("查询成功")
    .setData(user)
    .setTimestamp(System.currentTimeMillis());

// 也可以和静态工厂方法混用
Resultuser result = Result.ok()
    .setMessage("自定义成功消息")
    .setData(user);
```

### PageResult 使用示例

```java
PageResultuser page = new PageResultuser()
    .setTotal(100L)
    .setPageNum(1L)
    .setPageSize(10L)
    .setRecords(userList);
```

---

## 其他 `@Accessors` 参数


| 参数             | 说明                           | 示例                                |
| ---------------- | ------------------------------ | ----------------------------------- |
| `chain = true`   | setter 返回 this，支持链式调用 | `user.setName("a").setAge(1)`       |
| `fluent = true`  | 去掉 get/set 前缀              | `user.name("a").age(1)`             |
| `prefix = "abc"` | 忽略指定前缀的字段             | 字段`abcName` → getter `getName()` |

---

## 注意事项

1. **需要 Lombok 版本 ≥ 1.18.0**（你的项目使用 1.18.46，完全支持）
2. **与 `@Builder` 的区别**：

   - `@Accessors(chain = true)`：在现有对象上链式修改
   - `@Builder`：构建新对象，通常需要 `build()` 方法结束

```java
// @Accessors 方式：修改已有对象
user.setName("张三").setAge(25);

// @Builder 方式：创建新对象
User user = User.builder().name("张三").age(25).build();
```

3. **IDE 支持**：需要安装 Lombok 插件，否则会报红（但编译正常）
