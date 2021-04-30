package org.example;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.example.types.HadoopRecord;
import org.example.types.Notification;
import org.example.types.PosInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class PosConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PosConsumer.class);

    public static void main(String[] args) {

            Properties props = new Properties();
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfig.applicationID);
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.bootstrapServers);

            StreamsBuilder builder = new StreamsBuilder();
            KStream<String, PosInvoice> KS0 = builder.stream(AppConfig.posTopicName,
                                                             Consumed.with(AppSerdes.String(), AppSerdes.PosInvoice()));

            //Requirement 1 - Produce to shipment
            KStream<String, PosInvoice> KS1 = KS0.filter((key, value) ->
                                                             value.getDeliveryType().toString()
                                                                  .equalsIgnoreCase(AppConfig.DELIVERY_TYPE_HOME_DELIVERY));

            KS1.to(AppConfig.shipmentTopicName,
                   Produced.with(AppSerdes.String(), AppSerdes.PosInvoice()));

            //Requirement 2 - Produce to loyaltyHadoopRecord
            KStream<String, PosInvoice> KS3 = KS0.filter((key, value) ->
                                                             value.getCustomerType().toString()
                                                                  .equalsIgnoreCase(AppConfig.CUSTOMER_TYPE_PRIME));

            KStream<String, Notification> KS4 = KS3.mapValues(
                invoice -> RecordBuilder.getNotification(invoice)
                                                             );

            KS4.to(AppConfig.notificationTopic,
                   Produced.with(AppSerdes.String(), AppSerdes.Notification()));

            //Requirement 3 - Produce to Hadoop
            KStream<String, PosInvoice> KS6 = KS0.mapValues(
                invoice -> RecordBuilder.getMaskedInvoice(invoice)
                                                           );

            KStream<String, HadoopRecord> KS7 = KS6.flatMapValues(
                invoice -> RecordBuilder.getHadoopRecords(invoice)
                                                                 );

            KS7.to(AppConfig.hadoopTopic,
                   Produced.with(AppSerdes.String(), AppSerdes.HadoopRecord()));

            Topology posFanOutTopology = builder.build();

            logger.info("Starting the following topology");
            logger.info(posFanOutTopology.describe().toString());

            KafkaStreams myStream = new KafkaStreams(posFanOutTopology, props);
            myStream.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Stopping Stream");
                myStream.close();
            }));
        }

}
