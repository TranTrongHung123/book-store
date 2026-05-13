package com.ptit.backend.messaging;

import com.ptit.backend.config.RabbitMQConfig;
import com.ptit.backend.service.FlashSaleCustomerService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlashSaleCancelConsumer {

    private final FlashSaleCustomerService flashSaleCustomerService;

    @RabbitListener(queues = RabbitMQConfig.CANCEL_PROCESS_QUEUE, ackMode = "MANUAL")
    public void handleCancelMessage(FlashSaleMessage message,
                                     Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("[MQ Consumer] Received cancel message for reservation={}", message.getReservationId());

        try {
            flashSaleCustomerService.cancelReservation(message.getReservationId());
            channel.basicAck(deliveryTag, false);
            log.info("[MQ Consumer] Successfully cancelled reservation={}", message.getReservationId());
        } catch (Exception e) {
            log.error("[MQ Consumer] Failed to cancel reservation={}", message.getReservationId(), e);
            try {
                // Reject and don't requeue — will go to DLQ
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("[MQ Consumer] Failed to nack message", ex);
            }
        }
    }
}
