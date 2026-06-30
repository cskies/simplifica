package com.simplifica.repo;

import com.simplifica.entity.Document;
import com.simplifica.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findByUserOrderByAnalyzedAtDesc(User user, Pageable pageable);
    long countByUserAndAnalyzedAtMonthAndYear(User user, int month, int year);
}
