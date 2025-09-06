package com.members.controllers;

import com.members.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> listMember() {
        return ResponseEntity.ok("listado de members");
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok("by id");
    }

    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.ok("create");
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> update(@PathVariable UUID id) {
        return ResponseEntity.ok("update");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return ResponseEntity.noContent().build();
    }


}
