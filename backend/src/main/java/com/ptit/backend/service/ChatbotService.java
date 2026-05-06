package com.ptit.backend.service;

import com.ptit.backend.dto.request.ChatRequest;
import com.ptit.backend.dto.response.ChatMessageResponse;
import com.ptit.backend.dto.response.ChatSessionResponse;
import com.ptit.backend.dto.response.ChatbotResponse;
import java.util.List;
import java.util.Optional;

public interface ChatbotService {

    ChatSessionResponse createSession(Long userId);

    /**
     * Lấy session ACTIVE gần nhất của user đã đăng nhập.
     * Dùng để frontend restore session khi user mở lại chatbot.
     */
    Optional<ChatSessionResponse> getActiveSession(Long userId);

    /**
     * Xử lý tin nhắn của user và trả về phản hồi từ AI.
     *
     * Quy trình:
     * 1. Validate session
     * 2. RAG search (MySQL FULLTEXT + Author FULLTEXT)
     * 3. Load chat history từ MySQL
     * 4. Build prompt với RAG context
     * 5. Gọi Gemini API (qua Spring AI)
     * 6. Parse structured output (BeanOutputConverter)
     * 7. Persist messages vào DB
     * 8. Auto-truncate nếu quá nhiều tin nhắn
     */
    ChatbotResponse chat(ChatRequest request, Long userId);

    List<ChatMessageResponse> getChatHistory(Long sessionId);

    void closeSession(Long sessionId);
}
