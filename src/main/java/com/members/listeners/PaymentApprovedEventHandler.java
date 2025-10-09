package com.members.listeners;

import com.members.events.PaymentApprovedEvent;
import com.members.services.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovedEventHandler {
    private final MembershipService membershipService;

    @Transactional
    @KafkaListener(topics = "payment-approved-event-topic", groupId = "members-service")
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        log.info("Procesando pago aprovado {}", event);
        try {
            membershipService.createMembershipFromPayment(event);
            log.info("Membresia creada exitosamente para usuario: {}", event.userId());
        } catch (
                Exception e) {
            log.error("Error al procesar el pago aprobado: {}", event, e);
        }
    }

}
