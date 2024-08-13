package com.anhduc.mevabe.entity;

import com.anhduc.mevabe.enums.VoucherStatus;
import com.anhduc.mevabe.enums.VoucherType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
public class Voucher extends AuditAble{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String code;
    @Enumerated(EnumType.STRING)
    VoucherType voucherType;
    BigDecimal value;
    BigDecimal minOrderValue;
    int maxUses;
    int currentUses;
    LocalDateTime expiryDate;
    @Enumerated(EnumType.STRING)
    VoucherStatus status;
    @ManyToMany
    Set<Product> products;
    @OneToOne(mappedBy = "voucher")
    Order order;

}
