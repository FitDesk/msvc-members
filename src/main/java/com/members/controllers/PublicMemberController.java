package com.members.controllers;

import com.members.dto.member.MembersResponseDto;
import com.members.services.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/public/member")
@AllArgsConstructor
@Tag(name = "Miembros", description = "API para destionar los miembros del gimnacio")

public class PublicMemberController {
    private final MemberService memberService;

    @GetMapping("/{userId}")
    public ResponseEntity<MembersResponseDto> getMemberByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(memberService.findMemberById(userId));
    }

}
