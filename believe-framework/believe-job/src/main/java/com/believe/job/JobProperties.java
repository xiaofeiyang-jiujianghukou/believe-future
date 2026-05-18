package com.believe.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "believe.job")
public class JobProperties {

    private boolean enabled = true;
    private String adminAddresses = "http://127.0.0.1:8080/xxl-job-admin";
    private String appName = "believe-job-executor";
    private String accessToken = "default_token";
    private String ip;
    private int port = 9999;
    private String logPath = "/data/applogs/xxl-job/jobhandler";
    private int logRetentionDays = 30;
}
