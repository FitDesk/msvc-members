package com.members.repository;

import com.members.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID>, JpaSpecificationExecutor<MemberEntity> {

    Optional<MemberEntity> findMemberByDni(String dni);

    @Query("""
            SELECT m FROM MemberEntity m 
            LEFT JOIN FETCH MembershipEntity ms ON ms.userId = m.userId 
            WHERE m.userId = :userId 
            AND ms.status = 'ACTIVE' 
            AND ms.endDate > CURRENT_TIMESTAMP
            """)
    Optional<MemberEntity> findByIdWithActiveMembership(@Param("userId") UUID userId);
}
