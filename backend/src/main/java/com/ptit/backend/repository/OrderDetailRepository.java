package com.ptit.backend.repository;

import com.ptit.backend.entity.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

	List<OrderDetail> findByOrderOrderId(Long orderId);

	@Query("select coalesce(sum(od.quantity), 0) from OrderDetail od where od.book.bookId = :bookId")
	Long sumSoldQuantityByBookId(@Param("bookId") Long bookId);
}

