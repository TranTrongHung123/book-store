package com.ptit.backend.repository;

import com.ptit.backend.entity.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Load N tin nhắn gần nhất của session
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.session.sessionId = :sessionId ORDER BY m.createdAt DESC")
    List<ChatMessage> findTopNBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    /**
     * Load toàn bộ lịch sử hội thoại của session theo thứ tự thời gian
     */
    List<ChatMessage> findBySessionSessionIdOrderByCreatedAtAsc(Long sessionId);

    /**
     * Đếm tổng số tin nhắn trong session — dùng để quyết định có cần truncate không
     */
    long countBySessionSessionId(Long sessionId);

    /**
     * Xóa N tin nhắn cũ nhất của session (auto-truncation).
     */
    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM chat_message
            WHERE message_id IN (
                SELECT message_id FROM (
                    SELECT message_id FROM chat_message
                    WHERE session_id = :sessionId
                    ORDER BY created_at ASC
                    LIMIT :deleteCount
                ) AS oldest_msgs
            )
            """, nativeQuery = true)
    void deleteOldestMessages(@Param("sessionId") Long sessionId, @Param("deleteCount") int deleteCount);

    /**
     * Xóa toàn bộ tin nhắn của session
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.session.sessionId = :sessionId")
    void deleteAllBySessionSessionId(@Param("sessionId") Long sessionId);
}

