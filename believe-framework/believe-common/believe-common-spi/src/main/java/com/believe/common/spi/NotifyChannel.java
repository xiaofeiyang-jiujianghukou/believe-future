package com.believe.common.spi;

public interface NotifyChannel {

    void send(String recipient, String title, String content);

    String getChannel();
}
