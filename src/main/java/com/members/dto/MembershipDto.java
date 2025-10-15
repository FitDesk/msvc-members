package com.members.dto;

import com.members.enums.MembershipStatus;

import java.time.LocalDateTime;

public record MembershipDto(
        String planName,
        MembershipStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer daysRemaining,
        boolean isActive,
        boolean isExpired
) {
}