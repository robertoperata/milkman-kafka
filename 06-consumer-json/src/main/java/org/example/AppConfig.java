package org.example;

public class AppConfig {

    final static String applicationID = "PosFanOutApp";
    final static String bootstrapServers = "localhost:9092";
    final static String posTopicName = "pos";
    final static String shipmentTopicName = "shipment";
    final static String notificationTopic = "loyalty";
    final static String hadoopTopic = "hadoop-sink";
    final static String DELIVERY_TYPE_HOME_DELIVERY = "HOME-DELIVERY";
    final static String CUSTOMER_TYPE_PRIME = "PRIME";
    final static Double LOYALTY_FACTOR = 0.02;
}
