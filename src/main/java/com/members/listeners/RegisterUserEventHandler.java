package com.members.listeners;

import com.members.entity.MemberEntity;
import com.members.enums.MemberStatus;
import com.members.events.CreatedUserEvent;
import com.members.events.NotificationEvent;
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
    @KafkaListener(topics = "user-created-event-topic")
    public void handle(CreatedUserEvent userEvent) {
        log.info("Usuario nuevo recibido {}", userEvent);
        MemberEntity member = MemberEntity.builder()
                .userId(UUID.fromString(userEvent.userId()))
                .firstName(userEvent.firstName())
                .lastName(userEvent.lastName())
                .dni(userEvent.dni())
                .phone(userEvent.phone())
                .status(MemberStatus.ACTIVE)
                .build();

        memberRepository.save(member);
        log.info("Usuario guardado {}", member);
    }

}
