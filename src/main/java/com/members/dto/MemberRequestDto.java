package com.members.dto;

public record MemberRequestDto(
        String firstName,
        String lastName,
        String dni,
        String phone
) {
}
