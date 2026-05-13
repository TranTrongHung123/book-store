package com.ptit.backend.messaging;

import com.ptit.backend.config.RabbitMQConfig;
import com.ptit.backend.entity.FlashSaleItem;
import com.ptit.backend.repository.FlashSaleItemRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlashSaleOrderConsumer {

    private final FlashSaleItemRepository flashSaleItemRepository;

    /**
     * After payment confirmed, sync sold_quantity to MySQL.
     */
    @Transactional
    @RabbitListener(queues = RabbitMQConfig.ORDER_CONFIRMED_QUEUE, ackMode = "MANUAL")
    public void handleOrderConfirmed(FlashSaleMessage message,
                                      Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("[MQ Consumer] Order confirmed for reservation={}, item={}", message.getReservationId(), message.getFlashSaleItemId());

        try {
            FlashSaleItem item = flashSaleItemRepository.findById(message.getFlashSaleItemId())
                    .orElseThrow(() -> new RuntimeException("FlashSaleItem not found: " + message.getFlashSaleItemId()));

            int newSoldQty = (item.getSoldQuantity() != null ? item.getSoldQuantity() : 0) + message.getQuantity();
            item.setSoldQuantity(newSoldQty);
            flashSaleItemRepository.save(item);

            channel.basicAck(deliveryTag, false);
            log.info("[MQ Consumer] Synced sold_quantity for item={}, newSoldQty={}", message.getFlashSaleItemId(), newSoldQty);
        } catch (Exception e) {
            log.error("[MQ Consumer] Failed to process order confirmed", e);
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("[MQ Consumer] Failed to nack message", ex);
            }
        }
    }
}
