package com.members.services;

import com.members.dto.*;

import java.util.UUID;

public interface MemberService {

    MemberPageResponseDto findAllMembers(MemberFilterDto filters,
                                         int page,
                                         int size,
                                         String sortField,
                                         String sortDirection
    );

    MembersResponseDto findMemberById(UUID id);

    MembersResponseDto updateInformationMember(UUID id, MemberRequestDto dto);

    MemberWithSecurityDataDto findByMemberSecurity(UUID id);


    MemberInfoDTO getMemberInfo(UUID userId);
}
