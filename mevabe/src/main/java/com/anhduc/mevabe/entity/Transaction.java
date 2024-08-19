package com.anhduc.mevabe.entity;

import com.anhduc.mevabe.enums.*;
import com.anhduc.mevabe.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transactions")
public class Transaction extends AuditAble{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Enumerated(EnumType.STRING)
    TransactionType transactionType;
    BigDecimal price;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime transactionDate;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    Order order;
}
