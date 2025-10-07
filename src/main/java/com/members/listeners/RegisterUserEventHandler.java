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


//    @KafkaListener(
//            topics = "user-created-event-topic",
//            groupId = "members-service"
//    )
//    public void handleUserUpdatedEvent(CreatedUserEvent event) {
//        log.info("Evento recibido de usuario actualizado: {}", event);
//
//        try {
//            UUID userId = UUID.fromString(event.userId());
//
//            memberRepository.findById(userId).ifPresent(member -> {
//                if (event.firstName() != null) {
//                    member.setFirstName(event.firstName());
//                }
//                if (event.lastName() != null) {
//                    member.setLastName(event.lastName());
//                }
//                if (event.profileImageUrl() != null) {
//                    member.setProfileImageUrl(event.profileImageUrl());
//                }
//
//                memberRepository.save(member);
//                log.info("Miembro actualizado exitosamente: {}", member.getUserId());
//            });
//
//        } catch (
//                Exception e) {
//            log.error("Error al procesar evento de usuario actualizado: {}", event, e);
//        }
//    }

}
