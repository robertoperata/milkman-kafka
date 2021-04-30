package org.example;

import org.apache.kafka.streams.processor.StreamPartitioner;
import org.example.types.PosInvoice;

public class RewardsPartitioner implements StreamPartitioner<String, PosInvoice> {

    @Override
    public Integer partition(String topic, String key, PosInvoice value, int numPartitions) {

        return value.getCustomerCardNo().hashCode() % numPartitions;
    }
}
