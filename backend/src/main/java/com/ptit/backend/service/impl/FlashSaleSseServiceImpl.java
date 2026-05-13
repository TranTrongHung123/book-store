package com.ptit.backend.service.impl;

import com.ptit.backend.dto.response.FlashSaleStockUpdate;
import com.ptit.backend.service.FlashSaleSseService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class FlashSaleSseServiceImpl implements FlashSaleSseService {

    private static final long SSE_TIMEOUT = 5 * 60 * 1000L; // 5 minutes

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Override
    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            emitter.complete();
        });
        emitter.onError(e -> {
            emitters.remove(emitter);
            emitter.completeWithError(e);
        });

        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"status\":\"connected\"}"));
        } catch (IOException e) {
            emitters.remove(emitter);
            log.warn("[SSE] Failed to send initial event: {}", e.getMessage());
        }

        log.info("[SSE] New emitter connected. Total: {}", emitters.size());
        return emitter;
    }

    @Override
    public void broadcastStockUpdate(Long flashSaleItemId, int remainingStock, boolean soldOut) {
        FlashSaleStockUpdate update = FlashSaleStockUpdate.builder()
                .flashSaleItemId(flashSaleItemId)
                .remainingStock(remainingStock)
                .soldOut(soldOut)
                .build();

        List<SseEmitter> deadEmitters = new java.util.ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("stock-update")
                        .data(update));
            } catch (IOException e) {
                deadEmitters.add(emitter);
                log.debug("[SSE] Emitter removed due to error: {}", e.getMessage());
            }
        }

        emitters.removeAll(deadEmitters);

        if (!deadEmitters.isEmpty()) {
            log.info("[SSE] Removed {} dead emitters. Remaining: {}", deadEmitters.size(), emitters.size());
        }
    }
}
