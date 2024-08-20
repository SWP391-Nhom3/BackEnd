package com.anhduc.mevabe.dto.request;

import com.anhduc.mevabe.enums.VoucherType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {

    VoucherType type;
    BigDecimal value;
    Integer maxUses;
    LocalDateTime expiryDate;
}
