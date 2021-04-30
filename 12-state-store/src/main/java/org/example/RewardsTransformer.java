package org.example;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.example.types.Notification;
import org.example.types.PosInvoice;

public class RewardsTransformer implements ValueTransformer<PosInvoice, Notification> {

    private KeyValueStore<String, Double> stateStore;

    @Override
    public void init(ProcessorContext processorContext) {
        this.stateStore = processorContext.getStateStore(AppConfig.REWARDS_STORE_NAME);
    }

    @Override
    public Notification transform(PosInvoice posInvoice) {
        Notification notification = new Notification()
            .withInvoiceNumber(posInvoice.getInvoiceNumber())
            .withCustomerCardNo(posInvoice.getCustomerCardNo())
            .withTotalAmount(posInvoice.getTotalAmount())
            .withEarnedLoyaltyPoints(posInvoice.getTotalAmount() * AppConfig.LOYALTY_FACTOR)
            .withTotalLoyaltyPoint(0.0);
        Double accumulateRewards = stateStore.get(notification.getCustomerCardNo());
        Double totalRewards;
        if(accumulateRewards != null) {
            totalRewards = accumulateRewards + notification.getEarnedLoyaltyPoints();
        } else {
            totalRewards = notification.getEarnedLoyaltyPoints();
        }
        stateStore.put(notification.getCustomerCardNo(), totalRewards);
        notification.setTotalLoyaltyPoint(totalRewards);
        return notification;
    }

    @Override
    public void close() {

    }
}
