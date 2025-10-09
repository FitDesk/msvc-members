package com.members.repository;

import com.members.entity.MembershipEntity;
import com.members.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipEntity, UUID> {
    @Query("SELECT m FROM MembershipEntity m WHERE m.userId = :userId AND m.status = 'ACTIVE' ORDER BY m.endDate DESC LIMIT 1")
    Optional<MembershipEntity> findActiveMembershipByUserId(@Param("userId") UUID userId);

    List<MembershipEntity> findByUserIdAndStatus(UUID userId, MembershipStatus status);

    @Modifying
    @Query("UPDATE MembershipEntity m SET m.status = 'EXPIRED' WHERE m.status = 'ACTIVE' AND m.endDate < :currentDate")
    int updateExpiredMemberships(@Param("currentDate") LocalDateTime currentDate);


    @Query("SELECT COUNT(m) > 0 FROM MembershipEntity m WHERE m.userId = :userId AND m.status = 'ACTIVE' AND m.endDate > :currentDate")
    boolean hasActiveMembership(@Param("userId") UUID userId, @Param("currentDate") LocalDateTime currentDate);
}