package com.members.dto;

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
