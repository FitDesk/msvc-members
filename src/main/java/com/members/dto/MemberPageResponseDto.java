package com.members.dto;

import java.util.List;

public record MemberPageResponseDto(
        List<MembersResponseDto> members,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
