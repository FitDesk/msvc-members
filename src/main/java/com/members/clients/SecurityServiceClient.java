package com.members.clients;

import com.members.dto.security.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "msvc-security", path = "/users")
public interface SecurityServiceClient {

    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable UUID id);
}
