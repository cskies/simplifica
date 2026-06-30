package com.simplifica.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // bcrypt hashed

    private String name;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionPlan plan = SubscriptionPlan.FREE;

    @Column(nullable = false)
    @Builder.Default
    private Integer documentsUsedThisMonth = 0;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime planRenewsAt;

    public boolean canUseDocument() {
        if (plan == SubscriptionPlan.FREE) {
            return documentsUsedThisMonth < 5;
        }
        if (plan == SubscriptionPlan.PRO) {
            return documentsUsedThisMonth < 100;
        }
        return true; // BUSINESS unlimited
    }

    public void resetMonthlyQuota() {
        documentsUsedThisMonth = 0;
    }
}
