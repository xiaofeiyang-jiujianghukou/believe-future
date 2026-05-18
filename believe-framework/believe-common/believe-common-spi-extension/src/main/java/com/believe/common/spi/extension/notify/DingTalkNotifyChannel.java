package com.believe.common.spi.extension.notify;

import com.believe.common.spi.NotifyChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class DingTalkNotifyChannel implements NotifyChannel {

    private final HttpClient httpClient;
    private final String webhookUrl;

    public DingTalkNotifyChannel(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public void send(String recipient, String title, String content) {
        try {
            String body = String.format(
                    "{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\\n%s\"},\"at\":{\"atMobiles\":[\"%s\"]}}",
                    title, content, recipient);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                log.info("DingTalk message sent to {}: {}", recipient, title);
            } else {
                log.error("DingTalk failed for {}: status={}", recipient, response.statusCode());
            }
        } catch (Exception e) {
            log.error("DingTalk send error for {}: {}", recipient, e.getMessage(), e);
        }
    }

    @Override
    public String getChannel() {
        return "dingtalk";
    }
}
