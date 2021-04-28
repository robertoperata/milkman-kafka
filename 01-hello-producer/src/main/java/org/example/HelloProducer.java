package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;

import java.util.Properties;

public class HelloProducer {

    private static final Logger logger = LoggerFactory.getLogger(HelloProducer.class);

    public static void main(String[] args) {
        logger.info("Creating Kafka producer.");
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.bootstrapServers);
        properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, AppConfig.applicationId);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<Integer, String> producer = new KafkaProducer(properties);
        logger.info("Start sending messages...");

        for (int i = 0; i < AppConfig.numEvents; i++) {
            producer.send(new ProducerRecord<>(AppConfig.topicName, i, "SimpleMessage-" + i));
        }

        logger.info("Finished sending messages. Closing Kafka Producer.");
        producer.close();
    }

}
