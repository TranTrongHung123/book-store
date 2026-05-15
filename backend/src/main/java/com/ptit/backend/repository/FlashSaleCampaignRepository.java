package com.ptit.backend.repository;

import com.ptit.backend.entity.FlashSaleCampaign;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashSaleCampaignRepository extends JpaRepository<FlashSaleCampaign, Long> {

    /**
     * Lấy tất cả Flash Sale campaign đang active
     */
    @Query("""
            SELECT c FROM FlashSaleCampaign c
            WHERE (c.status = 'ACTIVE' OR c.status = 'UPCOMING')
              AND c.startTime <= :now
              AND c.endTime >= :now
            ORDER BY c.endTime ASC
            """)
    List<FlashSaleCampaign> findAllActiveCampaigns(@org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);
}
