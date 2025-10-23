package com.members.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoDTO {
    @JsonProperty("userId")
    private UUID userId;
    
    private String firstName;
    private String lastName;
    private String email;
    private String dni;
    private String phone;
    private String initials;  
    private String profileImageUrl;
    
    private String status;
    
    @JsonProperty("membership")
    private MembershipDto membership;
    
    private LocalDateTime lastAccess;
}
