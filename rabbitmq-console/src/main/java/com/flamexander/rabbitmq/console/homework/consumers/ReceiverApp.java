package com.flamexander.rabbitmq.console.homework.consumers;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ReceiverApp {
    private static final String EXCHANGE_NAME = "php_exchanger";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);;

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String queueName = channel.queueDeclare().getQueue();

            while (true) {
                String input = reader.readLine();
                if (input.equals("exit")) {
                    break;
                }
                String [] inputComand = input.split(" ");
                String command = inputComand[0];
                if (inputComand.length == 2 && command.equals("set_topic")) {
                    String topic = input.substring(command.length() + 1);
                    channel.queueBind(queueName, EXCHANGE_NAME, topic);
                    System.out.println("Вы подписались на тему " + topic);
                }else if (inputComand.length == 2 && command.equals("unsubscribe")) {
                    String topic = input.substring(command.length() + 1);
                    channel.queueUnbind(queueName, EXCHANGE_NAME, topic);
                    System.out.println("Вы отписались от темы " + topic);
                } else {
                    System.out.println("Введите команду в формате 'set_topic topic_name' или 'unsubscribe topic_name'");
                    continue;
                }
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                };
                channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                });
            }
        }
    }
}
