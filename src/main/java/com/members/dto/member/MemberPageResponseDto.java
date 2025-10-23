package com.members.dto.member;

import java.util.List;

public record MemberPageResponseDto(
        List<MemberWithMembershipDto> members,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
