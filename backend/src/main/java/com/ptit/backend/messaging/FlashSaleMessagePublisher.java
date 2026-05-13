package com.ptit.backend.messaging;

import com.ptit.backend.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlashSaleMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publish a delayed cancel message. The message sits in the delay queue
     * for delayMs milliseconds, then gets routed to the process queue.
     */
    public void publishDelayedCancel(FlashSaleMessage message, long delayMs) {
        MessagePostProcessor mpp = msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(delayMs));
            return msg;
        };

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.FLASH_SALE_EXCHANGE,
                RabbitMQConfig.CANCEL_ROUTING_KEY,
                message,
                mpp
        );
        log.info("[MQ] Published delayed cancel for reservation={}, delay={}ms", message.getReservationId(), delayMs);
    }

    /**
     * Publish order confirmed event for async processing.
     */
    public void publishOrderConfirmed(FlashSaleMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.FLASH_SALE_EXCHANGE,
                RabbitMQConfig.ORDER_CONFIRMED_ROUTING_KEY,
                message
        );
        log.info("[MQ] Published order confirmed for reservation={}", message.getReservationId());
    }
}
