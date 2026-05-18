# believe-common-security

安全工具模块，提供 AES 加解密等基础安全能力。基于 JDK 标准库，零额外依赖。

## 适用场景

- 敏感配置项加密存储（数据库密码、API Key 等）
- 数据传输中的字段级加密
- 密钥生成与管理

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-security</artifactId>
</dependency>
```

### 使用示例

**1. 生成密钥**

```java
String secretKey = AesUtil.generateKey();
// 输出: dGhpcyBpcyBhIDI1Ni1iaXQgQUVTIGtleSBnZW5lcmF0ZWQ=
// 安全保存此密钥，后续加解密都需要它
```

**2. 加密**

```java
String secretKey = AesUtil.generateKey();
String plainText = "13800138000";  // 手机号等敏感信息

String encrypted = AesUtil.encrypt(plainText, secretKey);
// 输出: xK8j3...（Base64 编码的密文，包含随机 IV）
```

**3. 解密**

```java
String decrypted = AesUtil.decrypt(encrypted, secretKey);
// 输出: 13800138000
```

**4. 实战：加密数据库密码**

```java
// 生成密钥（仅一次，安全保存到环境变量或密钥管理服务）
String key = AesUtil.generateKey();
// 将 key 存入环境变量: export BELIEVE_SECRET_KEY=dGhpcyBp...

// 加密数据库密码
String encryptedPwd = AesUtil.encrypt("MyDb@123", key);
// 将加密后的密码写入配置文件

// 应用启动时解密
@Value("${spring.datasource.password}")
private String encryptedPassword;

@Value("${BELIEVE_SECRET_KEY}")
private String secretKey;

@Bean
public DataSource dataSource() {
    String realPassword = AesUtil.decrypt(encryptedPassword, secretKey);
    // ...
}
```

## 加密细节

| 项目 | 说明 |
|------|------|
| 算法 | AES/GCM/NoPadding |
| 密钥长度 | 256 位 |
| IV | 12 字节随机生成，前置密文 |
| 认证标签 | 128 位（GCM 模式自带完整性校验） |
| 输出格式 | Base64 编码 |

> GCM 模式提供认证加密（AEAD），解密时自动校验数据完整性。若密文被篡改，解密直接抛异常。
