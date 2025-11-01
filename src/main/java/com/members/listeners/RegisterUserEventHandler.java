package com.members.listeners;

import com.members.entity.MemberEntity;
import com.members.events.CreatedUserEvent;
import com.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUserEventHandler {

    private final MemberRepository memberRepository;

    @Transactional
    @KafkaListener(
            topics = "user-created-event-topic",
            groupId = "members-service"
    )
    public void handle(CreatedUserEvent userEvent) {
        log.info("Usuario nuevo recibido {}", userEvent);

        try {
            UUID userId = UUID.fromString(userEvent.userId());
            if (memberRepository.findById(userId).isPresent()) {
                log.warn("Miembro con id {} ya existe , omitiendo creacion", userId);
                return;
            }
            MemberEntity member = MemberEntity.builder()
                    .userId(UUID.fromString(userEvent.userId()))
                    .firstName(userEvent.firstName())
                    .lastName(userEvent.lastName())
                    .dni(userEvent.dni())
                    .phone(userEvent.phone())
                    .profileImageUrl(userEvent.profileImageUrl())
                    .build();
            memberRepository.save(member);
            log.info("Usuario guardado {}", member);
        } catch (
                Exception e) {
            log.error("Error al procesar evento de usuario creado {}", userEvent, e);
        }

    }


}
