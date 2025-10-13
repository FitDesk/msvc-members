package com.members.controllers;

import com.members.dto.MemberPageResponseDto;
import com.members.dto.MemberRequestDto;
import com.members.dto.MembersResponseDto;
import com.members.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
@Tag(name = "Miembros", description = "API para destionar los miembros del gimnacio")

public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "Listado de miembros",
            description = "Trae todo los miembros de la base de datos")
    @GetMapping
    public ResponseEntity<MemberPageResponseDto> listMember(
            @RequestParam(required = false) String dni,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection

    ) {
        return ResponseEntity.ok(memberService.findAllMembers(dni, page, size, sortField, sortDirection));
    }

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @GetMapping("{id}")
    public ResponseEntity<MembersResponseDto> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(memberService.findMemberById(id));
    }

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @PatchMapping("{id}")
    public ResponseEntity<MembersResponseDto> update(@PathVariable UUID id, @Valid @RequestBody MemberRequestDto dto) {
        return ResponseEntity.ok(memberService.updateInformationMember(id, dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MembersResponseDto> getMemberByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(memberService.findMemberById(userId));
    }

}
