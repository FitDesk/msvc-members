package com.members.services;

import com.members.dto.image.ImageUploadResponseDto;
import com.members.dto.member.MemberFilterDto;
import com.members.dto.member.MemberPageResponseDto;
import com.members.dto.member.MemberRequestDto;
import com.members.dto.member.MembersResponseDto;
import com.members.dto.security.MemberWithSecurityDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface MemberService {

    MemberPageResponseDto findAllMembers(MemberFilterDto filters,
                                         int page,
                                         int size,
                                         String sortField,
                                         String sortDirection
    );

    MembersResponseDto findMemberById(UUID id);

    MembersResponseDto updateInformationMember(UUID id, MemberRequestDto dto, MultipartFile profileImage);

    MemberWithSecurityDataDto findByMemberSecurity(UUID id);


    ImageUploadResponseDto updateProfileImage(UUID userId, MultipartFile file);

    boolean deleteProfileImage(UUID userId);

    MemberInfoDTO getMemberInfo(UUID userId);

}
