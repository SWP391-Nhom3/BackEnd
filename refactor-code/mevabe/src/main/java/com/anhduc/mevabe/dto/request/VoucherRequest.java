package com.anhduc.mevabe.dto.request;

import com.anhduc.mevabe.enums.VoucherType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {

    VoucherType type;
    BigDecimal value;
    BigDecimal minOrderValue;
    Integer maxUses;
    LocalDate expiryDate;
}
