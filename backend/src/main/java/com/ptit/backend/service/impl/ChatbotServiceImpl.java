package com.ptit.backend.service.impl;

import com.ptit.backend.chatbot.memory.MySQLChatMemory;
import com.ptit.backend.chatbot.prompt.SystemPromptBuilder;
import com.ptit.backend.chatbot.rag.BookRagSearchService;
import com.ptit.backend.dto.request.ChatRequest;
import com.ptit.backend.dto.response.ChatMessageResponse;
import com.ptit.backend.dto.response.ChatSessionResponse;
import com.ptit.backend.dto.response.ChatbotResponse;
import com.ptit.backend.entity.ChatMessage;
import com.ptit.backend.entity.ChatSession;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.repository.ChatMessageRepository;
import com.ptit.backend.repository.ChatSessionRepository;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.ChatbotService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatModel chatModel;
    private final MySQLChatMemory chatMemory;
    private final BookRagSearchService ragSearchService;
    private final SystemPromptBuilder systemPromptBuilder;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Value("${chatbot.memory.max-messages:20}")
    private int maxMessages;

    /** Số tin nhắn gần nhất load vào history khi restore session */
    @Value("${chatbot.history.load-last-n:10}")
    private int historyLoadLastN;

    // ─── Session Management ───────────────────────────────────────────────────

    @Override
    @Transactional
    public ChatSessionResponse createSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setStartedAt(LocalDateTime.now());
        session.setStatus("ACTIVE");

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            session.setUser(user);
        }

        ChatSession saved = chatSessionRepository.save(session);
        log.info("Tạo session mới: {} (userId={})", saved.getSessionId(), userId);

        return buildSessionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatSessionResponse> getActiveSession(Long userId) {
        if (userId == null) return Optional.empty();

        List<ChatSession> activeSessions =
                chatSessionRepository.findActiveSessionsByUserId(userId);

        if (activeSessions.isEmpty()) return Optional.empty();

        // Lấy session ACTIVE mới nhất
        ChatSession session = activeSessions.get(0);
        log.info("Tìm thấy session ACTIVE: {} (userId={})", session.getSessionId(), userId);
        return Optional.of(buildSessionResponse(session));
    }

    @Override
    @Transactional
    public void closeSession(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.CHATBOT_SESSION_NOT_FOUND));
        session.setStatus("CLOSED");
        session.setEndedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
        log.info("Đóng session: {}", sessionId);
    }

    // ─── Chat Core ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ChatbotResponse chat(ChatRequest request, Long userId) {
        // 1. Validate session
        ChatSession session = chatSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new AppException(ErrorCode.CHATBOT_SESSION_NOT_FOUND));

        if ("CLOSED".equals(session.getStatus())) {
            throw new AppException(ErrorCode.CHATBOT_SESSION_NOT_FOUND);
        }

        // 2. Gắn userId vào session nếu user vừa đăng nhập nhưng session chưa có user
        if (userId != null && session.getUser() == null) {
            userRepository.findById(userId).ifPresent(session::setUser);
            chatSessionRepository.save(session);
        }

        String userMessage = request.message();
        String conversationId = session.getSessionId().toString();

        log.debug("Chat [session={}]: {}", conversationId, userMessage);

        // 3. RAG: Tìm context từ MySQL FULLTEXT search (book + author)
        String ragContext = ragSearchService.buildContextPayload(userMessage);

        // 4. Load N tin nhắn gần nhất từ lịch sử hội thoại
        List<Message> history = chatMemory.get(conversationId);

        // 5. Gọi Gemini với structured output
        ChatbotResponse aiResponse = callGeminiWithStructuredOutput(
                systemPromptBuilder.build(ragContext),
                userMessage,
                history
        );

        // 6. Persist tin nhắn USER vào DB
        saveMessage(session, "USER", userMessage);

        // 7. Persist tin nhắn BOT (lưu answer text)
        String botAnswer = (aiResponse.getAnswer() != null && !aiResponse.getAnswer().isBlank())
                ? aiResponse.getAnswer()
                : "Tôi xin lỗi, hiện tại tôi không thể trả lời. Vui lòng thử lại.";
        saveMessage(session, "BOT", botAnswer);

        // 8. Auto-truncate: xóa các tin nhắn cũ nếu vượt quá giới hạn
        autoTruncateHistory(session.getSessionId());

        return aiResponse;
    }


    /**
     * Gọi Gemini API thông qua Spring AI ChatModel.
     * Sử dụng BeanOutputConverter để ép AI trả về JSON theo schema của ChatbotResponse.
     */
    private ChatbotResponse callGeminiWithStructuredOutput(
            String systemPrompt, String userMessage, List<Message> history) {

        BeanOutputConverter<ChatbotResponse> converter =
                new BeanOutputConverter<>(ChatbotResponse.class);

        String fullSystemPrompt = systemPrompt
                + "\n\n═══ YÊU CẦU ĐỊNH DẠNG OUTPUT ═══\n"
                + "Trả lời theo cấu trúc JSON sau (KHÔNG thêm markdown code block, KHÔNG thêm ký tự nào khác ngoài JSON):\n"
                + converter.getFormat()
                + "\n\nLưu ý:"
                + "\n- Trường 'answer': câu trả lời NGẮN GỌN, thân thiện. TUYỆT ĐỐI KHÔNG liệt kê chi tiết từng cuốn sách hoặc danh sách mã giảm giá ở đây (hệ thống UI sẽ tự động hiển thị chúng ở dạng thẻ đẹp mắt). Chỉ cần nói 'Mình tìm thấy một số sách...' hoặc 'Bạn tham khảo các mã giảm giá bên dưới nhé'."
                + "\n- Trường 'intent': BOOK_SEARCH | PROMOTION | POLICY | RENTAL | POINTS | GENERAL"
                + "\n- Trường 'books': danh sách sách gợi ý (lấy đúng thông tin từ [DỮ LIỆU HỆ THỐNG], không được bịa)"
                + "\n- Trường 'promotion_info': copy toàn bộ phần 🎁 MÃ GIẢM GIÁ từ dữ liệu vào đây nếu có."
                + "\n- Trường 'has_results': true nếu tìm thấy sách/thông tin phù hợp";

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(fullSystemPrompt));
        messages.addAll(history);
        messages.add(new UserMessage(userMessage));

        try {
            ChatResponse response = chatModel.call(new Prompt(messages));
            String rawContent = response.getResult().getOutput().getText();

            log.debug("Raw Gemini response: {}", rawContent);

            // Làm sạch markdown code block nếu model bọc trong ```json ... ```
            String cleaned = rawContent.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```$", "").trim();
            }

            return converter.convert(cleaned);

        } catch (Exception e) {
            return handleAiError(e, userMessage);
        }
    }

    private ChatbotResponse handleAiError(Exception e, String userMessage) {
        String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        log.error("Lỗi khi gọi Gemini API: {}", e.getMessage(), e);

        if (errorMsg.contains("429") || errorMsg.contains("quota") || errorMsg.contains("rate limit")) {
            throw new AppException(ErrorCode.CHATBOT_AI_QUOTA_EXCEEDED);
        }

        if (errorMsg.contains("401") || errorMsg.contains("unauthorized") || errorMsg.contains("api key")) {
            throw new AppException(ErrorCode.CHATBOT_AI_UNAVAILABLE);
        }

        if (e instanceof com.fasterxml.jackson.core.JsonProcessingException
                || errorMsg.contains("json") || errorMsg.contains("parse")) {
            log.warn("AI trả về JSON không hợp lệ, dùng fallback response");
            return ChatbotResponse.builder()
                    .answer("Tôi hiểu câu hỏi của bạn về \"" + truncate(userMessage, 50)
                            + "\". Tuy nhiên tôi đang gặp sự cố kỹ thuật nhỏ. "
                            + "Vui lòng thử lại hoặc đặt câu hỏi cụ thể hơn nhé! 😊")
                    .intent("GENERAL")
                    .books(Collections.emptyList())
                    .hasResults(false)
                    .build();
        }

        // Lỗi chung
        throw new AppException(ErrorCode.CHATBOT_AI_UNAVAILABLE);
    }

    // ─── History ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Long sessionId) {
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new AppException(ErrorCode.CHATBOT_SESSION_NOT_FOUND);
        }

        // Chỉ load N tin nhắn gần nhất để tránh trả về quá nhiều
        PageRequest pageRequest = PageRequest.of(0, historyLoadLastN,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        List<ChatMessage> messages = chatMessageRepository.findTopNBySessionId(sessionId, pageRequest);
        // Đảo ngược để trả về theo thứ tự thời gian tăng dần
        Collections.reverse(messages);

        return messages.stream()
                .map(msg -> ChatMessageResponse.builder()
                        .messageId(msg.getMessageId())
                        .senderType(msg.getSenderType())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private ChatSessionResponse buildSessionResponse(ChatSession session) {
        return ChatSessionResponse.builder()
                .sessionId(session.getSessionId())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .userId(session.getUser() != null ? session.getUser().getUserId() : null)
                .build();
    }

    private void saveMessage(ChatSession session, String senderType, String content) {
        ChatMessage msg = ChatMessage.builder()
                .session(session)
                .senderType(senderType)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(msg);
    }

    /**
     * Giữ lại MAX_MESSAGES tin nhắn gần nhất.
     */
    private void autoTruncateHistory(Long sessionId) {
        long count = chatMessageRepository.countBySessionSessionId(sessionId);
        if (count > maxMessages) {
            int deleteCount = (int) (count - maxMessages);
            chatMessageRepository.deleteOldestMessages(sessionId, deleteCount);
            log.debug("Auto-truncate session {}: xóa {} tin nhắn cũ", sessionId, deleteCount);
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
