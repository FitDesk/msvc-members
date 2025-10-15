package com.members.controllers;

import com.members.annotations.AdminAccess;
import com.members.dto.*;
import com.members.enums.MembershipStatus;
import com.members.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            summary = "Listado de miembros con información de membresía",
            description = "Obtiene todos los miembros con información de su plan/membresía activa, " +
                    "con soporte para búsqueda y filtrado avanzado"
    )
    @GetMapping
    @AdminAccess
    public ResponseEntity<MemberPageResponseDto> listMembers(
            @Parameter(description = "Búsqueda global en DNI, email, nombre y apellido")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filtrar por DNI específico")
            @RequestParam(required = false) String dni,

            @Parameter(description = "Filtrar por email")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filtrar por nombre (búsqueda parcial)")
            @RequestParam(required = false) String firstName,

            @Parameter(description = "Filtrar por apellido (búsqueda parcial)")
            @RequestParam(required = false) String lastName,

            @Parameter(description = "Filtrar por estado de membresía: ACTIVE, EXPIRED, CANCELLED, SUSPENDED, PENDING")
            @RequestParam(required = false) MembershipStatus membershipStatus,

            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo por el cual ordenar")
            @RequestParam(defaultValue = "firstName") String sortField,

            @Parameter(description = "Dirección de ordenamiento: asc o desc")
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        MemberFilterDto filters = new MemberFilterDto(
                search,
                dni,
                email,
                firstName,
                lastName,
                membershipStatus
        );

        return ResponseEntity.ok(
                memberService.findAllMembers(filters, page, size, sortField, sortDirection)
        );
    }

    @AdminAccess
    @GetMapping("/user-security/{id}")
    public ResponseEntity<MemberWithSecurityDataDto> getMemberSecurity(@PathVariable UUID id) {
        return ResponseEntity.ok(memberService.findByMemberSecurity(id));
    }

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @GetMapping("/user/{id}")
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
