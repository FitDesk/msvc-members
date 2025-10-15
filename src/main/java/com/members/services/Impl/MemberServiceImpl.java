package com.members.services.Impl;

import com.members.clients.SecurityServiceClient;
import com.members.dto.*;
import com.members.dto.security.UserDTO;
import com.members.dto.security.UserSecurityDto;
import com.members.entity.MemberEntity;
import com.members.entity.MembershipEntity;
import com.members.enums.AuthProvider;
import com.members.exceptions.MemberNotFoundException;
import com.members.mapper.MemberMapper;
import com.members.repository.MemberRepository;
import com.members.repository.MembershipRepository;
import com.members.services.MemberService;
import com.members.specifications.MemberSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final MembershipRepository membershipRepository;
    private final SecurityServiceClient securityServiceClient;

    @Override
    @Transactional(readOnly = true)
    public MemberPageResponseDto findAllMembers(
            MemberFilterDto filters,
            int page,
            int size,
            String sortField,
            String sortDirection
    ) {
        log.info("Buscando miembros con filtros: {}", filters);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Specification<MemberEntity> spec = buildSpecification(filters);
        Page<MemberEntity> memberPage = memberRepository.findAll(spec, pageable);


        List<MemberWithMembershipDto> members = memberPage.getContent().stream()
                .map(member -> {

                    MembershipEntity activeMembership = membershipRepository
                            .findActiveMembershipByUserId(member.getUserId())
                            .orElse(null);

                    member.setActiveMembership(activeMembership);

                    String externalEmail = null;

                    try {
                        UserDTO userDTO = securityServiceClient.getUserById(member.getUserId());
                        externalEmail = userDTO.getEmail();
                    } catch (
                            Exception ex) {
                        log.error("Error al traer el email del usuario {} {}", member.getUserId(), ex.getMessage());
                    }

                    MemberWithMembershipDto dto = memberMapper.toDtoWithMembership(member);
                    return new MemberWithMembershipDto(
                            dto.userId(),
                            dto.firstName(),
                            dto.lastName(),
                            dto.initials(),
                            dto.dni(),
                            dto.phone(),
                            externalEmail != null ? externalEmail : dto.email(),
                            dto.profileImageUrl(),
                            dto.status(),
                            dto.membership()
                    );
                })
                .toList();
        log.info("Encontrados {} miembros de {} totales", members.size(), memberPage.getTotalElements());

        return new MemberPageResponseDto(
                members,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.isLast()
        );
    }

    private Specification<MemberEntity> buildSpecification(MemberFilterDto filters) {
        Specification<MemberEntity> spec = (root, query, criteriaBuilder) -> null;

        if (filters == null) {
            return spec;
        }

        if (filters.search() != null && !filters.search().isBlank()) {
            spec = spec.and(MemberSpecification.globalSearch(filters.search()));
        } else {
            if (filters.dni() != null && !filters.dni().isBlank()) {
                spec = spec.and(MemberSpecification.hasDni(filters.dni()));
            }
            if (filters.email() != null && !filters.email().isBlank()) {
                spec = spec.and(MemberSpecification.hasEmail(filters.email()));
            }
            if (filters.firstName() != null && !filters.firstName().isBlank()) {
                spec = spec.and(MemberSpecification.hasFirstName(filters.firstName()));
            }
            if (filters.lastName() != null && !filters.lastName().isBlank()) {
                spec = spec.and(MemberSpecification.hasLastName(filters.lastName()));
            }
        }

        if (filters.membershipStatus() != null) {
            spec = spec.and(MemberSpecification.hasMembershipStatus(filters.membershipStatus()));
        }

        return spec;
    }


    @Transactional(readOnly = true)
    @Override
    public MembersResponseDto findMemberById(UUID id) {
        return memberRepository.findById(id).map(memberMapper::toDto).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public MembersResponseDto updateInformationMember(UUID id, MemberRequestDto dto) {

        MemberEntity member = memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (dto.firstName() != null) {
            member.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            member.setLastName(dto.lastName());
        }
        if (dto.dni() != null) {
            member.setDni(dto.dni());
        }
        if (dto.phone() != null) {
            member.setPhone(dto.phone());
        }
        memberRepository.save(member);
        return memberMapper.toDto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberWithSecurityDataDto findByMemberSecurity(UUID id) {
        log.info("Buscando informacion de member con id {}", id);

        MemberEntity member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
        UserSecurityDto securityDto = null;

        try {
            UserDTO userDto = securityServiceClient.getUserById(id);
            securityDto = new UserSecurityDto(
                    userDto.getEmail(), AuthProvider.valueOf(userDto.getProvider() != null ? userDto.getProvider() : "LOCAL")
            );

            log.info("Datos de security obtenediros {} para el usuario {}", userDto, id);
        } catch (
                Exception ex) {
            log.error("Error al obtener datos de security para el usuario {} : {}", id, ex.getMessage());
        }

        MembershipDto membershipDto = null;

        try {
            MembershipEntity activeMembership = membershipRepository
                    .findActiveMembershipByUserId(member.getUserId())
                    .orElse(null);
            if (activeMembership != null) {
                member.setActiveMembership(activeMembership);
                membershipDto = memberMapper.mapActiveMembership(member);
                log.info("Membresia activa encontrada para el usuario {}", id);
            }
        } catch (
                Exception ex) {
            log.error("Error al obtener membresia activa para el usuario {} : {}", id, ex.getMessage());
        }

        return memberMapper.toDtoWithSecurityAndMembership(member, securityDto, membershipDto);
    }
}
