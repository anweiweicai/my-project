package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
//    需要将消息发送到 "mail" 队列时，你可以直接使用`RabbitTemplate`
//    或者其他方法发送消息到 "mail" 队列。Spring AMQP 将会自动处理消息发送到这个队列，并且确保队列的存在。
    @Bean("emailQueue")
    public Queue emailQueue(){
        return QueueBuilder
                .durable("mail")
                .build();
    }

}
