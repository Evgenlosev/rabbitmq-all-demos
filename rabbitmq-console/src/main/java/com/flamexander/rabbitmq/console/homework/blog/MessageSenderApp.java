package com.flamexander.rabbitmq.console.homework.blog;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MessageSenderApp {
    private final static String QUEUE_NAME = "php_queue";
    private final static String EXCHANGER_NAME = "php_exchanger";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.TOPIC);

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGER_NAME, "php");

            while (true) {
                String input = reader.readLine();
                if (input.equals("exit")) {
                    break;
                }
                String [] inputMessage = input.split(" ");
                String topic = inputMessage[0];
                if (inputMessage.length < 2){
                    System.out.println("Введите команду в формате 'тема сообщение'");
                    continue;
                }
                String message = input.substring(topic.length() + 1);
                channel.basicPublish(EXCHANGER_NAME, topic, null, message.getBytes());
                System.out.println("Опубликовано сообщение '" + message + "' с темой '" + topic + "'");
            }
        }
    }
}
