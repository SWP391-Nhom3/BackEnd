package com.anhduc.mevabe.dto.response;

import com.anhduc.mevabe.entity.Order;
import com.anhduc.mevabe.entity.Product;
import com.anhduc.mevabe.enums.VoucherStatus;
import com.anhduc.mevabe.enums.VoucherType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {

    UUID id;
    String code;
    VoucherType voucherType;
    BigDecimal value;
    int maxUses;
    int currentUses;
}
