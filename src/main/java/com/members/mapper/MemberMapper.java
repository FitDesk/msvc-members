package com.members.mapper;

import com.members.config.MapStructConfig;
import com.members.dto.MembersResponseDto;
import com.members.entity.MemberEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface MemberMapper {

    MembersResponseDto toDto(MemberEntity entity);

}
