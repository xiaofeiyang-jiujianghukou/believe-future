package com.believe.common.spi.extension.notify;

import com.believe.common.spi.NotifyChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class SmsNotifyChannel implements NotifyChannel {

    private final HttpClient httpClient;
    private final String apiUrl;

    public SmsNotifyChannel(String apiUrl) {
        this.apiUrl = apiUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public void send(String recipient, String title, String content) {
        try {
            String body = String.format("{\"phone\":\"%s\",\"content\":\"%s\"}", recipient, content);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                log.info("SMS sent to {}: {}", recipient, title);
            } else {
                log.error("SMS failed for {}: status={}", recipient, response.statusCode());
            }
        } catch (Exception e) {
            log.error("SMS send error for {}: {}", recipient, e.getMessage(), e);
        }
    }

    @Override
    public String getChannel() {
        return "sms";
    }
}
