package com.members.entity;

import com.members.config.audit.Audit;
import com.members.config.audit.AuditListener;
import com.members.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "memberships", indexes = {
        @Index(columnList = "user_id"),
        @Index(columnList = "status"),
        @Index(columnList = "end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
public class MembershipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "amount_paid", precision = 10, scale = 2, nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "mercado_pago_transaction_id")
    private String mercadoPagoTransactionId;

    @Embedded
    private Audit audit;

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE &&
                LocalDateTime.now().isBefore(endDate);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }
}