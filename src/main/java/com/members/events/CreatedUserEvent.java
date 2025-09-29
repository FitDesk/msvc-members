package com.members.events;

import java.util.UUID;

public record CreatedUserEvent(
        String userId,
        String firstName,
        String lastName,
        String dni,
        String phone
) {
}
