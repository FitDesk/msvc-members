package com.members.dto;

import com.members.enums.MembershipStatus;

public record MemberFilterDto(
        String search,
        String dni,
        String email,
        String firstName,
        String lastName,
        MembershipStatus membershipStatus
) {
}