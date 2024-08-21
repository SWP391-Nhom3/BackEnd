package com.anhduc.mevabe.entity;

import com.anhduc.mevabe.enums.ReportActionType;
import com.anhduc.mevabe.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Report extends AuditAble {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne
    Order order;

    @ManyToOne
    User customer;

    @ManyToOne
    User staff;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReportStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReportActionType actionType;

    @Column(nullable = false, length = 500)
    String reason;

    String note;

    @JsonProperty("createdAt")
    public LocalDateTime getCreatedAt() {
        return super.createdAt;
    }

    @JsonProperty("updatedAt")
    public LocalDateTime getUpdatedAt() {
        return super.updatedAt;
    }
}
