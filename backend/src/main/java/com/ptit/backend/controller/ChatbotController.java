package com.ptit.backend.controller;

import com.ptit.backend.dto.request.ChatRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.ChatMessageResponse;
import com.ptit.backend.dto.response.ChatSessionResponse;
import com.ptit.backend.dto.response.ChatbotResponse;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.ChatbotService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final UserRepository userRepository;

    /**
     * Tạo phiên chat mới.
     * Nếu user đã đăng nhập session sẽ được gắn với userId.
     * Nếu là khách (guest), session tạo với user = null.
     */
    @PostMapping("/session")
    public ApiResponse<ChatSessionResponse> createSession() {
        Long userId = getCurrentUserId();
        ChatSessionResponse session = chatbotService.createSession(userId);
        return ApiResponse.<ChatSessionResponse>builder()
                .code(200)
                .message("Phiên chat được tạo thành công")
                .result(session)
                .build();
    }

    /**
     * Lấy session ACTIVE hiện tại của user đã đăng nhập.
     * Frontend gọi endpoint này khi mở chatbot để restore session cũ thay vì tạo mới.
     * Trả về 404 (result = null) nếu không có session nào đang active.
     */
    @GetMapping("/session/active")
    public ApiResponse<ChatSessionResponse> getActiveSession() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.<ChatSessionResponse>builder()
                    .code(200)
                    .message("Chưa đăng nhập, không có session")
                    .result(null)
                    .build();
        }

        Optional<ChatSessionResponse> activeSession = chatbotService.getActiveSession(userId);
        return ApiResponse.<ChatSessionResponse>builder()
                .code(200)
                .message(activeSession.isPresent() ? "Tìm thấy session đang active" : "Không có session nào đang active")
                .result(activeSession.orElse(null))
                .build();
    }

    /**
     * Gửi tin nhắn đến chatbot và nhận phản hồi structured
     */
    @PostMapping("/chat")
    public ApiResponse<ChatbotResponse> chat(@RequestBody @Valid ChatRequest request) {
        Long userId = getCurrentUserId();
        ChatbotResponse response = chatbotService.chat(request, userId);
        return ApiResponse.<ChatbotResponse>builder()
                .code(200)
                .message("OK")
                .result(response)
                .build();
    }

    /**
     * Lấy N tin nhắn gần nhất của một session.
     */
    @GetMapping("/history/{sessionId}")
    public ApiResponse<List<ChatMessageResponse>> getHistory(@PathVariable Long sessionId) {
        List<ChatMessageResponse> history = chatbotService.getChatHistory(sessionId);
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .code(200)
                .message("OK")
                .result(history)
                .build();
    }

    /**
     * Kết thúc và đóng phiên chat.
     * Session status sẽ chuyển sang CLOSED và lịch sử được bảo toàn trong DB.
     */
    @DeleteMapping("/session/{sessionId}")
    public ApiResponse<Void> closeSession(@PathVariable Long sessionId) {
        chatbotService.closeSession(sessionId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Phiên chat đã kết thúc")
                .result(null)
                .build();
    }


    /**
     * Trích xuất userId từ JWT token (nếu user đã đăng nhập).
     * Trả về null nếu là guest (chưa đăng nhập).
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }

            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .map(user -> user.getUserId())
                    .orElse(null);

        } catch (Exception e) {
            log.debug("Không thể lấy userId từ authentication: {}", e.getMessage());
            return null;
        }
    }
}
