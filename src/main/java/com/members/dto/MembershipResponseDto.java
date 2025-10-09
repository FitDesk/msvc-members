package com.members.dto;

import com.members.enums.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MembershipResponseDto(
        UUID id,
        UUID userId,
        UUID paymentId,
        UUID planId,
        String planName,
        Integer durationMonths,
        BigDecimal amountPaid,
        LocalDateTime startDate,
        LocalDateTime endDate,
        MembershipStatus status,
        String externalReference,
        boolean isActive,
        boolean isExpired,
        long daysRemaining
) {}