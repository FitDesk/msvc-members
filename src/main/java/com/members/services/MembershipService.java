package com.members.services;

import com.members.dto.membership.MembershipResponseDto;
import com.members.events.PaymentApprovedEvent;

import java.util.UUID;

public interface MembershipService {
    MembershipResponseDto createMembershipFromPayment(PaymentApprovedEvent event);
    MembershipResponseDto getActiveMembership(UUID userId);
    boolean hasActiveMembership(UUID userId);
    void updateExpiredMemberships();
}