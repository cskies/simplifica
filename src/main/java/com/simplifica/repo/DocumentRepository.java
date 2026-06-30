package com.simplifica.repo;

import com.simplifica.entity.Document;
import com.simplifica.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findByUserOrderByAnalyzedAtDesc(User user, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.user = :user AND EXTRACT(MONTH FROM d.analyzedAt) = :month AND EXTRACT(YEAR FROM d.analyzedAt) = :year")
    long countByUserAndAnalyzedAtMonthAndYear(@Param("user") User user, @Param("month") int month, @Param("year") int year);
}
