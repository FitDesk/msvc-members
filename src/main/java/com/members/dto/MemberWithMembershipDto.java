package com.members.dto;

public record MemberWithMembershipDto(
        String userId,
        String firstName,
        String lastName,
        String initials,
        String dni,
        String phone,
        String email,
        String profileImageUrl,
        String status,
        MembershipDto membership
) {
}