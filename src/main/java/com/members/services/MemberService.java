package com.members.services;

import com.members.dto.MemberFilterDto;
import com.members.dto.MemberPageResponseDto;
import com.members.dto.MemberRequestDto;
import com.members.dto.MembersResponseDto;

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
}
