package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;
import java.util.logging.LogManager;

public class Dispatcher implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
    private static final String kafkaConfig = "/kafka.properties";


    private final KafkaProducer<Integer, String> producer;
    private final String topicName;
    private final String fileLocation;

    /**
     * A dispatcher thread takes a producer and sends Kafka messages to the given topic.
     * The Events are supplied in a file
     *
     * @param producer     A valid producer instance
     * @param topicName    Name of the Kafka topic
     * @param fileLocation File location containing events.
     *                     Location of file is relative to the class path.
     *                     each line in the file is considered an event.
     */
    Dispatcher(KafkaProducer<Integer, String> producer, String topicName, String fileLocation) {
        this.producer = producer;
        this.topicName = topicName;
        this.fileLocation = fileLocation;
    }

    @Override
    public void run() {
        logger.info("Start processing " + fileLocation + "...");
        File file = new File(fileLocation);
        int msgKey = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                producer.send(new ProducerRecord<>(topicName, msgKey, line));
                msgKey++;
            }
            logger.trace("Finished sending " + msgKey + " messages from " + fileLocation);
        } catch (Exception e) {
            logger.error("Exception in thread " + fileLocation);
            throw new RuntimeException(e);
        }
    }

}
