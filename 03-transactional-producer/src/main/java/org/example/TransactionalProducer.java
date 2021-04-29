package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TransactionalProducer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalProducer.class);

    public static void main(String[] args) {
        logger.info("Creating Kafka producer.");
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.bootstrapServers);
        properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, AppConfig.applicationId);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, AppConfig.applicationId);
        KafkaProducer<Integer, String> producer = new KafkaProducer(properties);

        producer.initTransactions();
        logger.info("Start fist transaction");
        producer.beginTransaction();
        try{
            for (int i = 0; i < AppConfig.numEvents; i++) {
                producer.send(new ProducerRecord<>(AppConfig.topicName1, i, "SimpleMessage-T1" + i));
                producer.send(new ProducerRecord<>(AppConfig.topicName2, i, "SimpleMessage-T2" + i));
            }
            logger.info("Committing first transaction");
            producer.commitTransaction();
        } catch(Exception e){
            logger.info("Exception in first transaction");
            producer.abortTransaction();
            producer.close();
            throw new RuntimeException(e);
        }

        logger.info("Start second transaction");
        producer.beginTransaction();
        try{
            for (int i = 0; i < AppConfig.numEvents; i++) {
                producer.send(new ProducerRecord<>(AppConfig.topicName1, i, "SimpleMessage-T1" + i));
                producer.send(new ProducerRecord<>(AppConfig.topicName2, i, "SimpleMessage-T2" + i));
            }
            logger.info("Committing second transaction");
            producer.abortTransaction();
        } catch(Exception e){
            logger.info("Exception in second transaction");
            producer.abortTransaction();
            producer.close();
            throw new RuntimeException(e);
        }


        logger.info("Finished sending messages. Closing Kafka Producer.");
        producer.close();

    }
}
