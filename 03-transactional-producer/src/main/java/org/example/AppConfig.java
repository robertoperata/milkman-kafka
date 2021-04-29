package org.example;

public class AppConfig {

    final static String applicationId = "HelloProducer";
    final static String bootstrapServers = "localhost:9092,localhost:9093";
    final static String topicName1 = "hello-producer-1";
    final static String topicName2 = "hello-producer-2";
    final static String transactionId = "hello-transaction";

    final static int numEvents = 2;

}
