package com.members.dto.member;

public record MembersResponseDto(
        String userId,
        String firstName,
        String lastName,
        String dni,
        String phone,
        String profileImageUrl,
        String status
) {
}
