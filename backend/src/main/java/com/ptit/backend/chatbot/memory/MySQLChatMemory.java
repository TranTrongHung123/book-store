package com.ptit.backend.chatbot.memory;

import com.ptit.backend.entity.ChatMessage;
import com.ptit.backend.entity.ChatSession;
import com.ptit.backend.repository.ChatMessageRepository;
import com.ptit.backend.repository.ChatSessionRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MySQLChatMemory implements ChatMemory {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    /**
     * Lưu danh sách messages vào DB.
     */
    @Override
    @Transactional
    public void add(String conversationId, List<Message> messages) {
        Long sessionId = parseSessionId(conversationId);
        if (sessionId == null) return;

        ChatSession session = chatSessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            log.warn("Không tìm thấy session với ID: {}", sessionId);
            return;
        }

        for (Message message : messages) {
            String senderType = switch (message.getMessageType().name()) {
                case "USER" -> "USER";
                case "ASSISTANT" -> "BOT";
                default -> null;
            };

            if (senderType == null || message.getText() == null) continue;

            ChatMessage chatMessage = ChatMessage.builder()
                    .session(session)
                    .senderType(senderType)
                    .content(message.getText())
                    .createdAt(LocalDateTime.now())
                    .build();
            chatMessageRepository.save(chatMessage);
        }
    }

    /**
     * Load N tin nhắn gần nhất của một session theo thứ tự thời gian
     */
    @Override
    @Transactional(readOnly = true)
    public List<Message> get(String conversationId) {
        int lastN = 20;
        Long sessionId = parseSessionId(conversationId);
        if (sessionId == null) return Collections.emptyList();


        PageRequest pageRequest = PageRequest.of(0, lastN,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        List<ChatMessage> dbMessages = chatMessageRepository
                .findTopNBySessionId(sessionId, pageRequest);


        Collections.reverse(dbMessages);

        return dbMessages.stream()
                .map(this::toSpringAiMessage)
                .collect(Collectors.toList());
    }

    /**
     * Xóa toàn bộ lịch sử của một session và đóng session.
     */
    @Override
    @Transactional
    public void clear(String conversationId) {
        Long sessionId = parseSessionId(conversationId);
        if (sessionId == null) return;

        chatMessageRepository.deleteAllBySessionSessionId(sessionId);

        chatSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setStatus("CLOSED");
            session.setEndedAt(LocalDateTime.now());
            chatSessionRepository.save(session);
        });

        log.info("Đã xóa lịch sử và đóng session: {}", sessionId);
    }


    private Message toSpringAiMessage(ChatMessage dbMsg) {
        return "USER".equals(dbMsg.getSenderType())
                ? new UserMessage(dbMsg.getContent())
                : new AssistantMessage(dbMsg.getContent());
    }

    private Long parseSessionId(String conversationId) {
        try {
            return Long.parseLong(conversationId);
        } catch (NumberFormatException e) {
            log.error("conversationId không hợp lệ: {}", conversationId);
            return null;
        }
    }
}
