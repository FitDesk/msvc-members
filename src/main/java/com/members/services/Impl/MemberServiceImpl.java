package com.members.services.Impl;

import com.members.dto.MemberPageResponseDto;
import com.members.dto.MemberRequestDto;
import com.members.dto.MembersResponseDto;
import com.members.entity.MemberEntity;
import com.members.mapper.MemberMapper;
import com.members.repository.MemberRepository;
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


    @Override
    @Transactional(readOnly = true)
    public MemberPageResponseDto findAllMembers(String dni, int page, int size, String sortField, String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Specification<MemberEntity> spec = MemberSpecification.hasDni(dni);
        Page<MemberEntity> memberPage = memberRepository.findAll(spec, pageable);

        List<MembersResponseDto> members = memberPage.getContent().stream().map(memberMapper::toDto).toList();

        return new MemberPageResponseDto(
                members,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.isLast()
        );
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
}
