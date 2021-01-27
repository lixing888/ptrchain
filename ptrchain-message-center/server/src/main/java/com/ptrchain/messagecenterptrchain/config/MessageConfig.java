package com.ptrchain.messagecenterptrchain.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MessageConfig {
    //是否装载未发送的消息
    @Value("${ptrchain.queue.isLoadNotSendMessage}")
    private boolean isLoadNotSendMessage;
    @Value("${ptrchain.queue.messagePath}")
    private String messagePath;
}
