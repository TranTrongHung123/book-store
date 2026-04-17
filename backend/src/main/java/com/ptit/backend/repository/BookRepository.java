package com.ptit.backend.repository;

import com.ptit.backend.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("""
            select distinct bc.book
            from BookCategory bc
            where lower(bc.category.name) = lower(:categoryName)
            """)
    Page<Book> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("""
            select b
            from Book b
            where (:q is null or lower(b.title) like lower(concat('%', :q, '%')))
              and (:categoryName is null or exists (
                    select 1
                    from BookCategory bc
                    where bc.book = b
                      and lower(bc.category.name) = lower(:categoryName)
              ))
            """)
    Page<Book> searchBooks(@Param("q") String q, @Param("categoryName") String categoryName, Pageable pageable);

    @Modifying
    @Query("""
            update Book b
            set b.totalStock = b.totalStock - :quantity
            where b.bookId = :bookId
              and b.totalStock >= :quantity
            """)
    int decreaseStockIfAvailable(@Param("bookId") Long bookId, @Param("quantity") int quantity);
}

