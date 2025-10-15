package com.members.mapper;

import com.members.config.MapStructConfig;
import com.members.dto.MemberWithMembershipDto;
import com.members.dto.MembersResponseDto;
import com.members.dto.MembershipDto;
import com.members.entity.MemberEntity;
import com.members.entity.MembershipEntity;
import com.members.enums.MembershipStatus;
import com.members.helpers.MemberHelpers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(config = MapStructConfig.class)
public interface MemberMapper {

    MembersResponseDto toDto(MemberEntity entity);

    @Mapping(target = "membership", source = "entity", qualifiedByName = "mapActiveMembership")
    @Mapping(target = "initials", source = "entity", qualifiedByName = "mapInitials")
    MemberWithMembershipDto toDtoWithMembership(MemberEntity entity);

    @Named("mapInitials")
    default String mapInitials(MemberEntity entity) {
        return MemberHelpers.generateInitials(entity.getFirstName(), entity.getLastName());
    }

    @Named("mapActiveMembership")
    default MembershipDto mapActiveMembership(MemberEntity entity) {

        MembershipEntity activeMembership = entity.getActiveMembership();

        if (activeMembership == null) {
            return null;
        }

        boolean isActuallyActive = activeMembership.getStatus() == MembershipStatus.ACTIVE
                && activeMembership.getEndDate().isAfter(LocalDateTime.now());

        int daysRemaining = calculateDaysRemaining(activeMembership);

        return new MembershipDto(
                activeMembership.getPlanName(),
                activeMembership.getStatus(),
                activeMembership.getStartDate(),
                activeMembership.getEndDate(),
                daysRemaining,
                isActuallyActive,
                activeMembership.isExpired()
        );
    }

    default int calculateDaysRemaining(MembershipEntity membership) {
        if (membership.getEndDate() == null) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(membership.getEndDate())) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(now, membership.getEndDate());
    }
}
