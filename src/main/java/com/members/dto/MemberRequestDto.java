package com.members.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberRequestDto(
        String firstName,
        String lastName,
        String dni,
        String phone
) {
}
