package com.ptit.backend.repository;

import com.ptit.backend.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserUserId(Long userId, Pageable pageable);

    List<Order> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    long countByOrderStatusContainingIgnoreCase(String orderStatus);
    List<Order> findByPaymentStatusAndPaymentMethodAndCreatedAtBefore(String orderStatus, String paymentMethod, LocalDateTime now);

}
