package com.believe.common.spi.extension.autoconfigure;

import com.believe.common.spi.DataSerializer;
import com.believe.common.spi.IdGenerator;
import com.believe.common.spi.NotifyChannel;
import com.believe.common.spi.extension.id.LeafSegmentIdGenerator;
import com.believe.common.spi.extension.id.SnowflakeIdGenerator;
import com.believe.common.spi.extension.notify.DingTalkNotifyChannel;
import com.believe.common.spi.extension.notify.EmailNotifyChannel;
import com.believe.common.spi.extension.notify.SmsNotifyChannel;
import com.believe.common.spi.extension.serializer.JsonDataSerializer;
import com.believe.common.spi.extension.serializer.ProtobufDataSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

import javax.sql.DataSource;

@AutoConfiguration
public class SpiExtensionAutoConfiguration {

    // ==================== NotifyChannel ====================

    @Bean
    @ConditionalOnMissingBean(name = "smsNotifyChannel")
    @ConditionalOnProperty(prefix = "believe.spi.notify.sms", name = "api-url")
    public SmsNotifyChannel smsNotifyChannel(
            @org.springframework.beans.factory.annotation.Value("${believe.spi.notify.sms.api-url}") String apiUrl) {
        return new SmsNotifyChannel(apiUrl);
    }

    @Bean
    @ConditionalOnMissingBean(name = "emailNotifyChannel")
    @ConditionalOnClass(JavaMailSender.class)
    @ConditionalOnBean(JavaMailSender.class)
    @ConditionalOnProperty(prefix = "believe.spi.notify.email", name = "from")
    public EmailNotifyChannel emailNotifyChannel(JavaMailSender mailSender,
            @org.springframework.beans.factory.annotation.Value("${believe.spi.notify.email.from}") String from) {
        return new EmailNotifyChannel(mailSender, from);
    }

    @Bean
    @ConditionalOnMissingBean(name = "dingTalkNotifyChannel")
    @ConditionalOnProperty(prefix = "believe.spi.notify.dingtalk", name = "webhook-url")
    public DingTalkNotifyChannel dingTalkNotifyChannel(
            @org.springframework.beans.factory.annotation.Value("${believe.spi.notify.dingtalk.webhook-url}") String webhookUrl) {
        return new DingTalkNotifyChannel(webhookUrl);
    }

    // ==================== IdGenerator ====================

    @Bean
    @ConditionalOnMissingBean(IdGenerator.class)
    public SnowflakeIdGenerator snowflakeIdGenerator(
            @org.springframework.beans.factory.annotation.Value("${believe.spi.id.snowflake.worker-id:1}") long workerId,
            @org.springframework.beans.factory.annotation.Value("${believe.spi.id.snowflake.datacenter-id:1}") long datacenterId) {
        return new SnowflakeIdGenerator(workerId, datacenterId);
    }

    @Bean
    @ConditionalOnMissingBean(name = "leafSegmentIdGenerator")
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = "believe.spi.id.leaf", name = "biz-tag")
    public LeafSegmentIdGenerator leafSegmentIdGenerator(DataSource dataSource,
            @org.springframework.beans.factory.annotation.Value("${believe.spi.id.leaf.biz-tag}") String bizTag,
            @org.springframework.beans.factory.annotation.Value("${believe.spi.id.leaf.step:1000}") int step) {
        return new LeafSegmentIdGenerator(dataSource, bizTag, step);
    }

    // ==================== DataSerializer ====================

    @Bean
    @ConditionalOnMissingBean(name = "jsonDataSerializer")
    public JsonDataSerializer jsonDataSerializer() {
        return new JsonDataSerializer();
    }

    @Bean
    @ConditionalOnMissingBean(name = "protobufDataSerializer")
    @ConditionalOnClass(com.google.protobuf.MessageLite.class)
    public ProtobufDataSerializer protobufDataSerializer() {
        return new ProtobufDataSerializer();
    }
}
