package org.example;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.example.types.DeliveryAddress;
import org.example.types.HadoopRecord;
import org.example.types.Notification;
import org.example.types.PosInvoice;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;

public class AppSerdes extends Serdes {

    static Serde<PosInvoice> PosInvoice() {
        Serde<PosInvoice> serde = new SpecificAvroSerde<>();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("schema.registry.url", "http://localhost:8081");
        serde.configure(serdeConfigs, false);

        return serde;
    }
    
    static Serde<Notification> Notification() {
        Serde<Notification> serde = new SpecificAvroSerde<>();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("schema.registry.url", "http://localhost:8081");
        serde.configure(serdeConfigs, false);

        return serde;
    }
    
    static Serde<HadoopRecord> HadoopRecord() {
        Serde<HadoopRecord> serde = new SpecificAvroSerde<>();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("schema.registry.url", "http://localhost:8081");
        serde.configure(serdeConfigs, false);

        return serde;
    }


}
