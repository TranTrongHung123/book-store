package com.ptit.backend.repository;

import com.ptit.backend.entity.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    Page<InventoryLog> findByActionType(String actionType, Pageable pageable);
}

