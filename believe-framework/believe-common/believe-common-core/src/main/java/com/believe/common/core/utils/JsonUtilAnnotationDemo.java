package com.believe.common.core.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class JsonUtilAnnotationDemo {

    public static void main(String[] args) {
        // 测试 @JsonProperty、@JsonIgnore、@JsonFormat 注解
        System.out.println("========== 注解序列化测试 ==========");
        Order order = new Order();
        order.setOrderId(10086L);
        order.setOrderNo("ORD-20260518-001");
        order.setStatus(1);
        order.setAmount(299.99);
        order.setCreateTime(LocalDateTime.now());
        order.setInternalRemark("内部备注，应该被忽略");

        String json = JsonUtil.toJson(order);
        System.out.println("序列化结果:");
        System.out.println(json);

        System.out.println("\n========== 注解反序列化测试 ==========");
        // 测试 JSON 中 status 字段为字符串的情况（@JsonFormat 作用于反序列化）
        String jsonWithStringStatus = "{\"orderId\":10086,\"orderNo\":\"ORD-20260518-001\",\"status\":\"1\",\"amount\":299.99,\"createTime\":\"2026-05-18 10:00:00\"}";
        Order deserializedOrder = JsonUtil.fromJson(jsonWithStringStatus, Order.class);
        System.out.println("反序列化结果:");
        System.out.println("orderId: " + deserializedOrder.getOrderId());
        System.out.println("orderNo: " + deserializedOrder.getOrderNo());
        System.out.println("status: " + deserializedOrder.getStatus() + " (类型: " + ((Object)deserializedOrder.getStatus()).getClass().getSimpleName() + ")");
        System.out.println("amount: " + deserializedOrder.getAmount());
        System.out.println("createTime: " + deserializedOrder.getCreateTime());
        System.out.println("internalRemark (应被忽略): " + deserializedOrder.getInternalRemark());
    }

    // 测试用的 POJO 类，包含各种 Jackson 注解
    public static class Order {
        private Long orderId;

        @JsonProperty("orderNumber")  // 序列化时字段名变为 orderNumber
        private String orderNo;

        @JsonFormat(shape = JsonFormat.Shape.STRING)  // 状态码序列化为字符串
        private Integer status;

        private Double amount;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 自定义日期格式
        private LocalDateTime createTime;

        @JsonIgnore  // 完全忽略该字段
        private String internalRemark;

        // getter/setter
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

        public String getInternalRemark() { return internalRemark; }
        public void setInternalRemark(String internalRemark) { this.internalRemark = internalRemark; }
    }
}