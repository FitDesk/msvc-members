package com.members.exceptions;

import java.util.UUID;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(UUID id) {
        super("Miembro con id " + id + "no se pudo encontrar");
    }
}
