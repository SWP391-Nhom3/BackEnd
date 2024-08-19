package com.anhduc.mevabe.entity;

import com.anhduc.mevabe.enums.VoucherStatus;
import com.anhduc.mevabe.enums.VoucherType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class UserVoucher extends AuditAble{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @ManyToOne
    User user;
    @ManyToOne
    Voucher voucher;
    @CreatedDate
    @Column(nullable = false)
    LocalDateTime receivedAt;
    LocalDateTime usedAt;
}
