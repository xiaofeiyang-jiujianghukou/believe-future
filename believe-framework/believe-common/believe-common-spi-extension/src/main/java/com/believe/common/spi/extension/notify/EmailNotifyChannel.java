package com.believe.common.spi.extension.notify;

import com.believe.common.spi.NotifyChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
public class EmailNotifyChannel implements NotifyChannel {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailNotifyChannel(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(String recipient, String title, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(recipient);
            message.setSubject(title);
            message.setText(content);
            mailSender.send(message);
            log.info("Email sent to {}: {}", recipient, title);
        } catch (Exception e) {
            log.error("Email send error for {}: {}", recipient, e.getMessage(), e);
        }
    }

    @Override
    public String getChannel() {
        return "email";
    }
}
