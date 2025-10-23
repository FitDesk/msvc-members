package com.members.services.Impl;

import com.members.clients.SecurityServiceClient;
import com.members.dto.image.ImageUploadResponseDto;
import com.members.dto.member.*;
import com.members.dto.membership.MembershipDto;
import com.members.dto.security.MemberWithSecurityDataDto;
import com.members.dto.security.UserDTO;
import com.members.dto.security.UserSecurityDto;
import com.members.entity.MemberEntity;
import com.members.entity.MembershipEntity;
import com.members.enums.AuthProvider;
import com.members.exceptions.MemberNotFoundException;
import com.members.mapper.MemberMapper;
import com.members.repository.MemberRepository;
import com.members.repository.MembershipRepository;
import com.members.services.CloudinaryService;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final CloudinaryService cloudinaryService;

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
        log.info("Buscando miembro por ID: {}", id);
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        return memberMapper.toDto(member);
    }


    @Override
    @Transactional
    public MembersResponseDto updateInformationMember(UUID id, MemberRequestDto dto, MultipartFile profileImage) {
        log.info("Actualizando miembro: {}", id);

        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        if (dto.firstName() != null && !dto.firstName().isBlank()) {
            member.setFirstName(dto.firstName());
            log.debug("Actualizado firstName: {}", dto.firstName());
        }
        if (dto.lastName() != null && !dto.lastName().isBlank()) {
            member.setLastName(dto.lastName());
            log.debug("Actualizado lastName: {}", dto.lastName());
        }
        if (dto.dni() != null && !dto.dni().isBlank()) {
            member.setDni(dto.dni());
            log.debug("Actualizado DNI: {}", dto.dni());
        }
        if (dto.phone() != null && !dto.phone().isBlank()) {
            member.setPhone(dto.phone());
            log.debug("Actualizado phone: {}", dto.phone());
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String oldPublicId = cloudinaryService.extractPublicIdFromUrl(member.getProfileImageUrl());
            ImageUploadResponseDto uploadResponse = cloudinaryService.updateProfileImage(
                    profileImage,
                    id,
                    oldPublicId
            );
            member.setProfileImageUrl(uploadResponse.getUrl());
            log.info("Imagen de perfil actualizada en carpeta members");
        }

        MemberEntity savedMember = memberRepository.save(member);
        log.info("Miembro actualizado exitosamente");

        return memberMapper.toDto(savedMember);
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

    @Override
    @Transactional
    public ImageUploadResponseDto updateProfileImage(UUID userId, MultipartFile file) {
        log.info("🔄 Actualizando solo imagen de perfil para miembro: {}", userId);

        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));

        String oldPublicId = cloudinaryService.extractPublicIdFromUrl(member.getProfileImageUrl());
        ImageUploadResponseDto uploadResponse = cloudinaryService.updateProfileImage(file, userId, oldPublicId);

        member.setProfileImageUrl(uploadResponse.getUrl());
        memberRepository.save(member);

        log.info("✅ Imagen de miembro guardada en: fitdesk/members/profiles/");
        return uploadResponse;
    }

    @Override
    @Transactional
    public boolean deleteProfileImage(UUID userId) {
        log.info("Eliminando imagen de perfil de miembro: {}", userId);

        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));

        String currentImageUrl = member.getProfileImageUrl();

        if (currentImageUrl != null && (currentImageUrl.contains("googleusercontent.com") ||
                currentImageUrl.contains("ggpht.com"))) {
            log.warn("No se puede eliminar imagen de Google");
            return false;
        }

        String publicId = cloudinaryService.extractPublicIdFromUrl(currentImageUrl);
        boolean deleted = false;

        if (publicId != null) {
            deleted = cloudinaryService.deleteImage(publicId);
        }

        member.setProfileImageUrl(null);
        memberRepository.save(member);

        log.info("Referencia de imagen eliminada");
        return deleted;

    @Transactional(readOnly = true)
    public MemberInfoDTO getMemberInfo(UUID userId) {
        log.info("Obteniendo información completa del miembro {} para microservicio", userId);
        
        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));
        String email = null;
        try {
            UserDTO userDTO = securityServiceClient.getUserById(member.getUserId());
            email = userDTO.getEmail();
            log.info("Email obtenido: {}", email);
        } catch (Exception ex) {
            log.warn("No se pudo obtener email del usuario {} desde msvc-security: {}",
                    member.getUserId(), ex.getMessage());
        }
        MembershipDto membershipDto = null;
        try {
            MembershipEntity activeMembership = membershipRepository
                    .findActiveMembershipByUserId(member.getUserId())
                    .orElse(null);
            if (activeMembership != null) {
                member.setActiveMembership(activeMembership);
                membershipDto = memberMapper.mapActiveMembership(member);
                log.info("Membresía activa encontrada: {}", membershipDto.planName());
            } else {
                log.info("No hay membresía activa para el usuario {}", userId);
            }
        } catch (Exception ex) {
            log.error("Error al obtener membresía activa para el usuario {} : {}", userId, ex.getMessage());
        }
        
        MemberInfoDTO result = memberMapper.toMemberInfoDTO(member, email, membershipDto);
        log.info("MemberInfoDTO construido para {}: {} {}", userId, result.getFirstName(), result.getLastName());
        
        return result;

    }
}
