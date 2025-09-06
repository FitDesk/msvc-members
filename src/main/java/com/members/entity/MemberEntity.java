package com.members.entity;

import com.members.config.Audit;
import com.members.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "members", indexes = {@Index(columnList = "user_id"), @Index(columnList = "email")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    @Column(name = "membership_id")
    private UUID membershipId;
    @Embedded
    private Audit audit;

}
