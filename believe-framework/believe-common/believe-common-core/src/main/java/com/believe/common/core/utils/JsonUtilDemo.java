package com.believe.common.core.utils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class JsonUtilDemo {

    public static void main(String[] args) {
        // 测试数据
        Map<String, Object> data = new HashMap<>();
        data.put("name", "believe");
        data.put("version", 1.0);
        data.put("time", LocalDateTime.now());

        // 1. 测试序列化
        System.out.println("========== 序列化测试 ==========");
        String json = JsonUtil.toJson(data);
        System.out.println("序列化结果:");
        System.out.println(json);

        // 2. 测试反序列化
        System.out.println("\n========== 反序列化测试 ==========");
        // 使用类型安全的 TypeReference 处理泛型
        Map<String, Object> result = JsonUtil.fromJson(json, Map.class);
        System.out.println("反序列化结果:");
        System.out.println("name: " + result.get("name"));
        System.out.println("version: " + result.get("version"));
        System.out.println("time: " + result.get("time"));

        // 3. 测试 User 对象
        System.out.println("\n========== 对象序列化测试 ==========");
        User user = new User();
        user.setId(1001L);
        user.setUsername("xiaofeiyang");
        user.setCreateTime(LocalDateTime.now());

        String userJson = JsonUtil.toJson(user);
        System.out.println("User 序列化: " + userJson);

        User deserializedUser = JsonUtil.fromJson(userJson, User.class);
        System.out.println("User 反序列化: " + deserializedUser);
    }

    // 测试用的 POJO 类
    public static class User {
        private Long id;
        private String username;
        private LocalDateTime createTime;

        // getter/setter 必须有，Jackson 需要它们
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

        @Override
        public String toString() {
            return "User{id=" + id + ", username='" + username + "', createTime=" + createTime + "}";
        }
    }
}