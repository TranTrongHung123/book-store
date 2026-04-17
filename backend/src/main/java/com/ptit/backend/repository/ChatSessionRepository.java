package com.ptit.backend.repository;

import com.ptit.backend.entity.ChatSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Page<ChatSession> findByUserUserId(Long userId, Pageable pageable);

    /**
     * Tìm session ACTIVE của user
     */
    @Query("""
            SELECT s FROM ChatSession s
            WHERE s.user.userId = :userId
              AND s.status = 'ACTIVE'
            ORDER BY s.startedAt DESC
            """)
    List<ChatSession> findActiveSessionsByUserId(@Param("userId") Long userId);

    /**
     * Tìm session mới nhất của user
     */
    Optional<ChatSession> findTopByUserUserIdOrderByStartedAtDesc(Long userId);
}

