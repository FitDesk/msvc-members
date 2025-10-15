package com.members.dto;

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
