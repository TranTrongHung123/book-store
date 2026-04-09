package com.ptit.backend.repository;

import com.ptit.backend.entity.BookImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImportRepository extends JpaRepository<BookImport, Long> {
}

