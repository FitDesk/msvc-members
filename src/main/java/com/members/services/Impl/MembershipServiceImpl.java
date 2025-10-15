package com.members.services.Impl;

import com.members.dto.MembershipResponseDto;
import com.members.entity.MembershipEntity;
import com.members.enums.MembershipStatus;
import com.members.events.PaymentApprovedEvent;
import com.members.mapper.MembershipMapper;
import com.members.repository.MembershipRepository;
import com.members.services.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipMapper membershipMapper;

    @Override
    @Transactional
    public MembershipResponseDto createMembershipFromPayment(PaymentApprovedEvent event) {
        log.info("Creando membresía para usuario: {} con plan: {}", event.userId(), event.planName());

        List<MembershipEntity> activeMemberships = membershipRepository.findByUserIdAndStatus(
                event.userId(), MembershipStatus.ACTIVE);

        activeMemberships.forEach(membership -> {
            membership.setStatus(MembershipStatus.CANCELLED);
            log.info("Cancelando membresía anterior: {}", membership.getId());
        });

        membershipRepository.saveAll(activeMemberships);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(event.durationMonths(), startDate);

        MembershipEntity membership = MembershipEntity.builder()
                .userId(event.userId())
                .paymentId(event.paymentId())
                .planId(event.planId())
                .planName(event.planName())
                .durationMonths(event.durationMonths())
                .amountPaid(event.amount())
                .startDate(startDate)
                .endDate(endDate)
                .status(MembershipStatus.ACTIVE)
                .externalReference(event.externalReference())
                .mercadoPagoTransactionId(event.mercadoPagoTransactionId())
                .build();

        MembershipEntity savedMembership = membershipRepository.save(membership);
        log.info(" Membresía creada exitosamente: {} para usuario: {}", savedMembership.getId(), event.userId());

        return membershipMapper.entityToDto(savedMembership);
    }


    @Override
    @Transactional(readOnly = true)
    public MembershipResponseDto getActiveMembership(UUID userId) {
        log.info("Buscando membresía activa para usuario: {}", userId);

        return membershipRepository.findActiveMembershipByUserId(userId)
                .map(membershipMapper::entityToDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveMembership(UUID userId) {
        return membershipRepository.hasActiveMembership(userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateExpiredMemberships() {
        int updated = membershipRepository.updateExpiredMemberships(LocalDateTime.now());
        if (updated > 0) {
            log.info("Actualizadas {} membresías expiradas", updated);
        }
    }

    private LocalDateTime calculateEndDate(Integer durationMonths, LocalDateTime startDate) {
        return startDate.plusMonths(durationMonths);
    }
}