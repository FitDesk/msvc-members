package com.members.mapper;

import com.members.config.MapStructConfig;
import com.members.dto.MembershipResponseDto;
import com.members.entity.MembershipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(config = MapStructConfig.class)
public interface MembershipMapper {

    @Mapping(target = "isActive", expression = "java(entity.isActive())")
    @Mapping(target = "isExpired", expression = "java(entity.isExpired())")
    @Mapping(target = "daysRemaining", expression = "java(calculateDaysRemaining(entity))")
    MembershipResponseDto entityToDto(MembershipEntity entity);

    default long calculateDaysRemaining(MembershipEntity entity) {
        if (entity.getEndDate() == null)
            return 0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(entity.getEndDate()))
            return 0;
        return ChronoUnit.DAYS.between(now, entity.getEndDate());
    }
}