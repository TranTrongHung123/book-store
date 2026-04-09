package com.ptit.backend.repository;

import com.ptit.backend.entity.Promotion;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCode(String code);

    Page<Promotion> findByStatus(Integer status, Pageable pageable);
}

