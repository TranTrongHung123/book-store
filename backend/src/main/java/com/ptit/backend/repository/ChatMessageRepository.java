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
     * Nạp N tin nhắn gần nhất của session.
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.session.sessionId = :sessionId ORDER BY m.createdAt DESC")
    List<ChatMessage> findTopNBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    /**
     * Nạp toàn bộ lịch sử hội thoại của session theo thứ tự thời gian.
     */
    List<ChatMessage> findBySessionSessionIdOrderByCreatedAtAsc(Long sessionId);

    /**
     * Đếm tổng số tin nhắn trong session để biết có cần cắt bớt không.
     */
    long countBySessionSessionId(Long sessionId);

    /**
     * Xóa N tin nhắn cũ nhất của session.
     */
    @Query("SELECT c.messageId FROM ChatMessage c WHERE c.session.sessionId = :sessionId ORDER BY c.createdAt ASC")
    List<Long> findOldestMessageIds(@Param("sessionId") Long sessionId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChatMessage c WHERE c.messageId IN :ids")
    void deleteMessagesByIds(@Param("ids") List<Long> ids);

    @Transactional
    default void deleteOldestMessages(Long sessionId, int deleteCount) {
        List<Long> ids = findOldestMessageIds(sessionId,
                org.springframework.data.domain.PageRequest.of(0, deleteCount));
        if (!ids.isEmpty()) {
            deleteMessagesByIds(ids);
        }
    }

    /**
     * Xóa toàn bộ tin nhắn của session
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.session.sessionId = :sessionId")
    void deleteAllBySessionSessionId(@Param("sessionId") Long sessionId);
}
