package com.ptit.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // === Exchanges ===
    public static final String FLASH_SALE_EXCHANGE = "flash-sale.exchange";
    public static final String FLASH_SALE_DLX = "flash-sale.dlx";

    // === Queues ===
    public static final String CANCEL_DELAY_QUEUE = "flash-sale.cancel.delay.queue";
    public static final String CANCEL_PROCESS_QUEUE = "flash-sale.cancel.process.queue";
    public static final String ORDER_CONFIRMED_QUEUE = "flash-sale.order.confirmed.queue";
    public static final String DLQ = "flash-sale.dlq";

    // === Routing Keys ===
    public static final String CANCEL_ROUTING_KEY = "flash-sale.cancel";
    public static final String ORDER_CONFIRMED_ROUTING_KEY = "flash-sale.order.confirmed";

    @Bean
    public TopicExchange flashSaleExchange() {
        return new TopicExchange(FLASH_SALE_EXCHANGE, true, false);
    }

    @Bean
    public FanoutExchange flashSaleDlx() {
        return new FanoutExchange(FLASH_SALE_DLX, true, false);
    }

    /**
     * Delay queue: messages sit here with per-message TTL.
     * When TTL expires, messages are routed to the process queue via DLX mechanism.
     */
    @Bean
    public Queue cancelDelayQueue() {
        return QueueBuilder.durable(CANCEL_DELAY_QUEUE)
                .deadLetterExchange(FLASH_SALE_EXCHANGE)
                .deadLetterRoutingKey("flash-sale.cancel.process")
                .build();
    }

    /**
     * Actual processing queue — consumer picks up expired cancel messages here.
     */
    @Bean
    public Queue cancelProcessQueue() {
        return QueueBuilder.durable(CANCEL_PROCESS_QUEUE)
                .deadLetterExchange(FLASH_SALE_DLX)
                .build();
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return QueueBuilder.durable(ORDER_CONFIRMED_QUEUE)
                .deadLetterExchange(FLASH_SALE_DLX)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    // === Bindings ===

    @Bean
    public Binding cancelDelayBinding() {
        return BindingBuilder.bind(cancelDelayQueue())
                .to(flashSaleExchange())
                .with(CANCEL_ROUTING_KEY);
    }

    @Bean
    public Binding cancelProcessBinding() {
        return BindingBuilder.bind(cancelProcessQueue())
                .to(flashSaleExchange())
                .with("flash-sale.cancel.process");
    }

    @Bean
    public Binding orderConfirmedBinding() {
        return BindingBuilder.bind(orderConfirmedQueue())
                .to(flashSaleExchange())
                .with(ORDER_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(flashSaleDlx());
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
