package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.datagenerator.InvoiceGenerator;
import org.example.types.PosInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class RunnableProducer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RunnableProducer.class);
    private final AtomicBoolean stopper = new AtomicBoolean(false);
    private final KafkaProducer<String, PosInvoice> producer;
    private final String topicName;
    private InvoiceGenerator invoiceGenerator;
    private int produceSpeed;
    private int id;

    RunnableProducer(int id, KafkaProducer<String, PosInvoice> producer, String topicName, int produceSpeed) {
        this.topicName = topicName;
        this.producer = producer;
        this.invoiceGenerator = InvoiceGenerator.getInstance();
        this.produceSpeed = produceSpeed;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            logger.info("Starting producer " + id);
            while (!stopper.get()) {
                PosInvoice posInvoice = invoiceGenerator.getNextInvoice();
                producer.send(new ProducerRecord<>(topicName, posInvoice.getStoreID(), posInvoice));
                Thread.sleep(produceSpeed);
            }

        } catch (Exception e) {
            logger.error("Exception in Producer " + id);
            throw new RuntimeException(e);
        }
    }

    void shutdown() {
        logger.info("Shutting down producer " + id);
        stopper.set(true);

    }
}
