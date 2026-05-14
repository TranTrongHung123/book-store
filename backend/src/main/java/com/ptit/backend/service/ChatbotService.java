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
     * Lấy session ACTIVE gần nhất của người dùng đã đăng nhập.
     * Dùng để frontend khôi phục session khi người dùng mở lại chatbot.
     */
    Optional<ChatSessionResponse> getActiveSession(Long userId);

    /**
     * Xử lý tin nhắn của người dùng và trả về phản hồi từ AI.
     *
     * Quy trình:
     * 1. Kiểm tra session
     * 2. Tìm RAG bằng FULLTEXT trong MySQL
     * 3. Nạp lịch sử chat từ MySQL
     * 4. Tạo prompt kèm context RAG
     * 5. Gọi Gemini API (qua Spring AI)
     * 6. Parse output theo schema
     * 7. Lưu tin nhắn vào DB
     * 8. Tự cắt bớt nếu quá nhiều tin nhắn
     */
    ChatbotResponse chat(ChatRequest request, Long userId);

    List<ChatMessageResponse> getChatHistory(Long sessionId);

    void closeSession(Long sessionId);
}
