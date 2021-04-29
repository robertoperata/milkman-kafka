package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class MultiThreadProducer {
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadProducer.class);
    private static final String kafkaConfig = "/kafka.properties";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please provide command line arguments: topicName EventFiles");
            System.exit(-1);
        }

        String topicName = args[0];
        String[] eventFiles = Arrays.copyOfRange(args, 1, args.length);

        Properties properties = new Properties();
        try {
            InputStream configStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
            properties.load(configStream);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        } catch (IOException e) {
            logger.error("Cannot open Kafka config " + kafkaConfig);
            throw new RuntimeException(e);
        }

        logger.trace("Starting dispatcher threads...");
        KafkaProducer<Integer, String> producer = new KafkaProducer<>(properties);
        Thread[] dispatchers = new Thread[eventFiles.length];
        for (int i = 0; i < eventFiles.length; i++) {
            dispatchers[i] = new Thread(new Dispatcher(producer, topicName, eventFiles[i]));
            dispatchers[i].start();
        }

        try {
            for (Thread t : dispatchers)
                t.join();
        } catch (InterruptedException e) {
            logger.error("Thread Interrupted ");
        } finally {
            producer.close();
            logger.info("Finished dispatcher demo - Closing Kafka Producer.");
        }
    }

}
