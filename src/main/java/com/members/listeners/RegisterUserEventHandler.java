package com.members.listeners;

import com.members.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUserEventHandler {

    @Transactional
    @KafkaListener(topics = "user-created-event-topic")
    public void handle(NotificationEvent message) {
        log.info("Mensaje recibido {}", message);
    }

}
