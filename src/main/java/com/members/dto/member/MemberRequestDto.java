package com.members.dto.member;

import jakarta.validation.constraints.Size;

public record MemberRequestDto(
        @Size(max = 50,message = "El nombre es demasiado largo")
        String firstName,
        @Size(max = 50,message = "El apellido es demasiado largo")
        String lastName,
        String dni,
        String phone
) {
}
