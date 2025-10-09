package com.members.dto;

import java.util.List;

public record UserMembershipsResponseDto(
        MembershipResponseDto activeMembership,
        List<MembershipResponseDto> membershipHistory,
        boolean hasActiveMembership,
        int totalMemberships
) {}