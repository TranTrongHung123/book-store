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

	@Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END FROM OrderDetail od WHERE od.order.orderId = :orderId AND od.flashSaleItem IS NOT NULL")
	boolean existsFlashSaleItemByOrderId(@Param("orderId") Long orderId);

	@Query("""
		SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END
		FROM OrderDetail od
		WHERE od.order.user.userId = :userId
		  AND od.book.bookId = :bookId
		  AND lower(od.order.orderStatus) IN :completedStatuses
		""")
	boolean existsCompletedPurchase(
			@Param("userId") Long userId,
			@Param("bookId") Long bookId,
			@Param("completedStatuses") List<String> completedStatuses
	);
}

