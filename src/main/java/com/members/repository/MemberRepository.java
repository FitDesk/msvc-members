package com.members.repository;

import com.members.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID>, JpaSpecificationExecutor<MemberEntity> {

    Optional<MemberEntity> findMemberByDni(String dni);

}
