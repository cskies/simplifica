package com.simplifica.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalText;

    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String resumo;

    @Column(columnDefinition = "TEXT")
    private String pontosAtencao;

    private String veredicto;

    @Column(columnDefinition = "TEXT")
    private String veredictoMotivo;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime analyzedAt = LocalDateTime.now();

    private String title; // user can title their document
}
