package com.simplifica.dto;

import com.simplifica.entity.SubscriptionPlan;

public record AuthResponse(
    String token,
    String email,
    String name,
    SubscriptionPlan plan,
    Integer documentsRemaining,
    String message
) {}
