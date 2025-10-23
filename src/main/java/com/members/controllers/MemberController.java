package com.members.controllers;

import com.members.annotations.AdminAccess;
import com.members.dto.image.ImageUploadResponseDto;
import com.members.dto.member.MemberFilterDto;
import com.members.dto.member.MemberPageResponseDto;
import com.members.dto.member.MemberRequestDto;
import com.members.dto.member.MembersResponseDto;
import com.members.dto.security.MemberWithSecurityDataDto;
import com.members.enums.MembershipStatus;
import com.members.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
@Tag(name = "Miembros", description = "API para gestionar los miembros del gimnasio")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "Listado de miembros con información de membresía",
            description = "Obtiene todos los miembros con información de su plan/membresía activa y foto de perfil. " +
                    "Todas las fotos se almacenan en Cloudinary en la carpeta: fitdesk/members/profiles/"
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

            @Parameter(description = "Filtrar por estado de membresía")
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

    @Operation(
            summary = "Obtener miembro con datos de seguridad",
            description = "Obtiene información completa del miembro incluyendo email, provider y foto de perfil"
    )
    @AdminAccess
    @GetMapping("/user-security/{id}")
    public ResponseEntity<MemberWithSecurityDataDto> getMemberSecurity(@PathVariable UUID id) {
        return ResponseEntity.ok(memberService.findByMemberSecurity(id));
    }

    @Operation(
            summary = "Obtener miembro por ID",
            description = "Obtiene la información básica del miembro incluyendo su foto de perfil"
    )
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @GetMapping("/user/{id}")
    public ResponseEntity<MembersResponseDto> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(memberService.findMemberById(id));
    }

    @Operation(
            summary = "Actualizar información del miembro",
            description = "Actualiza los datos del miembro con soporte para actualización parcial. " +
                    "Puedes enviar solo los campos que deseas actualizar. " +
                    "Si incluyes una imagen, se actualizará la foto de perfil y se guardará en: fitdesk/members/profiles/. " +
                    "Las imágenes de Google serán reemplazadas automáticamente."
    )
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @PatchMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MembersResponseDto> update(
            @PathVariable UUID id,

            @Parameter(description = "Datos del miembro en formato JSON (campos opcionales)")
            @RequestPart(value = "member", required = false) @Valid MemberRequestDto dto,

            @Parameter(description = "Archivo de imagen de perfil (JPG/PNG/GIF/WEBP, máx 5MB)")
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        MemberRequestDto memberDto = dto != null ? dto : new MemberRequestDto(null, null, null, null);

        return ResponseEntity.ok(
                memberService.updateInformationMember(id, memberDto, profileImage)
        );
    }

    @Operation(
            summary = "Actualizar solo foto de perfil",
            description = "Endpoint dedicado para actualizar únicamente la foto de perfil del miembro. " +
                    "La imagen se guardará en Cloudinary en: fitdesk/members/profiles/. " +
                    "Elimina automáticamente la foto anterior (excepto si es de Google)."
    )
    @PostMapping(value = "/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#userId, authentication)")
    public ResponseEntity<ImageUploadResponseDto> updateProfileImage(
            @Parameter(description = "ID del usuario")
            @PathVariable UUID userId,

            @Parameter(description = "Archivo de imagen (JPG, PNG, GIF, WEBP, máx 5MB)")
            @RequestParam("file") MultipartFile file
    ) {
        ImageUploadResponseDto response = memberService.updateProfileImage(userId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar foto de perfil",
            description = "Elimina la foto de perfil del miembro de Cloudinary. " +
                    "Las imágenes de Google no pueden ser eliminadas."
    )
    @DeleteMapping("/{userId}/profile-image")
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#userId, authentication)")
    public ResponseEntity<Void> deleteProfileImage(
            @Parameter(description = "ID del usuario")
            @PathVariable UUID userId
    ) {
        boolean deleted = memberService.deleteProfileImage(userId);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
    }

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#userId,authentication)")
    @GetMapping("/{userId}")
    public ResponseEntity<MembersResponseDto> getMemberByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(memberService.findMemberById(userId));
    }


    @Operation(
            description = "Endpoint específico para comunicación entre microservicios. " +
                    "Incluye email desde msvc-security y membership activa completa."
    )
    @GetMapping("/{userId}/info")
    public ResponseEntity<MemberInfoDTO> getMemberInfo(@PathVariable UUID userId) {
        return ResponseEntity.ok(memberService.getMemberInfo(userId));
    }

}

