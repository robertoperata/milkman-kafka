package org.example;

public class AppConfig {

    final static String applicationID = "PosFanOutApp";
    final static String bootstrapServers = "localhost:9092";
    final static String posTopicName = "pos";
    final static String notificationTopic = "loyalty";
    final static String CUSTOMER_TYPE_PRIME = "PRIME";
    final static Double LOYALTY_FACTOR = 0.02;
    final static String REWARDS_STORE_NAME = "CustomerRewardsStore";
    final static String REWARDS_TEMP_TOPIC = "CustomerRewardsTemp";
}
