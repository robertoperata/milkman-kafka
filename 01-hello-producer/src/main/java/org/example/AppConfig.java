package org.example;

public class AppConfig {

    final static String applicationId = "HelloProducer";
    final static String bootstrapServers = "localhost:9092,localhost:9093";
    final static String topicName = "hello-producer-topic";
    final static int numEvents = 1000000;

}
