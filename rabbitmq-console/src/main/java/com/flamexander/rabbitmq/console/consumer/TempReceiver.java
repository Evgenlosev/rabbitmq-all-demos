package com.flamexander.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;

public class TempReceiver {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("myqueue", false, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            System.out.println(new String(delivery.getBody(), "UTF-8"));
        };
        channel.basicConsume("myqueue", true, deliverCallback, consumerTag -> { });
    }
}
