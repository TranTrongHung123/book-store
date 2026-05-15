package com.ptit.backend.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface FlashSaleSseService {

    SseEmitter createEmitter();

    void broadcastStockUpdate(Long flashSaleItemId, int remainingStock, boolean soldOut);
}
