package com.members.entity;

import com.members.config.audit.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "members", indexes = {@Index(columnList = "user_id")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String dni;
    private String phone;
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    @Embedded
    private Audit audit;

}
