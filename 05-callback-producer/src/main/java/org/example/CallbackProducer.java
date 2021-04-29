package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Properties;

public class CallbackProducer {

    private static final Logger logger = LoggerFactory.getLogger(CallbackProducer.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide command line arguments: topicName numEvents");
            System.exit(-1);
        }
        String topicName = args[0];
        int numEvents = Integer.valueOf(args[1]);
        logger.debug("topicName=" + topicName + ", numEvents=" + numEvents);

        logger.trace("Creating Kafka Producer...");
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "CallbackProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
        logger.trace("Start sending messages...");

        for (int j = 1; j <= numEvents; j++) {
            int i = j;
            producer.send(new ProducerRecord<>(topicName, i, "Simple Message-" + i),
                          (recordMetadata, e) -> {
                              if (e != null)
                                  logger.error("Error sending message with key " + i + " Error - " + e.getMessage());
                              else
                                  logger.info("Message " + i + " persisted with offset " + recordMetadata.offset()
                                              + " and timestamp on " + new Timestamp(recordMetadata.timestamp()));
                          });
        }

        logger.info("Finished Application - Closing Kafka Producer.");
        producer.close();
    }


}
