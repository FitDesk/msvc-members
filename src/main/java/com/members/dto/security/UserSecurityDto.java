package com.members.dto.security;

import com.members.enums.AuthProvider;

public record UserSecurityDto(
        String email,
        AuthProvider provider
) {
}
