package org.example;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.example.types.Notification;
import org.example.types.PosInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class RewardsApp{

    private static final Logger logger = LoggerFactory.getLogger(RewardsApp.class);

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfig.applicationID);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.bootstrapServers);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, PosInvoice> ks0 = builder.stream(
                                                    AppConfig.posTopicName,
                                                    Consumed.with(AppSerdes.String(), AppSerdes.PosInvoice()))
                                                 .filter((dey, value) ->
                                                    AppConfig.CUSTOMER_TYPE_PRIME.equalsIgnoreCase(value.getCustomerType()));


        StoreBuilder kvStoreBuilder = Stores.keyValueStoreBuilder(
            Stores.inMemoryKeyValueStore(AppConfig.REWARDS_STORE_NAME),
            AppSerdes.String(), AppSerdes.Double());

        builder.addStateStore(kvStoreBuilder);

        ks0.transformValues(() -> new RewardsTransformer(), AppConfig.REWARDS_STORE_NAME)
           .to(AppConfig.notificationTopic,
               Produced.with(AppSerdes.String(), AppSerdes.Notification()));

        KafkaStreams streams = new KafkaStreams(builder.build(), properties);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("stopping streams");
            streams.close();
        }));
    }
}
