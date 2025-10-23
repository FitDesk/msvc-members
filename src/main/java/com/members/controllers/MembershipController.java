package com.members.controllers;

import com.members.dto.membership.MembershipResponseDto;
import com.members.services.AuthorizationService;
import com.members.services.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/memberships")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Membresías", description = "API para gestión de membresías de usuarios")
public class MembershipController {

    private final MembershipService membershipService;
    private final AuthorizationService authorizationService;


    @Operation(summary = "Obtener membresía activa del usuario autenticado")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/my-active-membership")
    public ResponseEntity<MembershipResponseDto> getMyActiveMembership(Authentication authentication) {
        UUID userId = authorizationService.getUserId(authentication);
        log.info("🔍 Usuario {} consultando su membresía activa", userId);

        MembershipResponseDto membership = membershipService.getActiveMembership(userId);
        if (membership == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membership);
    }

    @Operation(summary = "Verificar si tiene membresía activa")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/has-active")
    public ResponseEntity<Boolean> hasActiveMembership(Authentication authentication) {
        UUID userId = authorizationService.getUserId(authentication);
        boolean hasActive = membershipService.hasActiveMembership(userId);
        return ResponseEntity.ok(hasActive);
    }


    @Operation(summary = "Obtener membresía activa de cualquier usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<MembershipResponseDto> getUserActiveMembership(@PathVariable UUID userId) {
        log.info("Admin consultando membresía activa del usuario: {}", userId);

        MembershipResponseDto membership = membershipService.getActiveMembership(userId);
        if (membership == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membership);
    }


}