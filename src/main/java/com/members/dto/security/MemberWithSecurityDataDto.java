package com.members.dto.security;

import com.members.dto.membership.MembershipDto;

public record MemberWithSecurityDataDto(
        String userId,
        String firstName,
        String lastName,
        String dni,
        String phone,
        String profileImageUrl,
        String status,
        String email,
        String provider,
        MembershipDto membership
) {
}
