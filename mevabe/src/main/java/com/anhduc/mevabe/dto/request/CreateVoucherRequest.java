package com.anhduc.mevabe.dto.request;

import com.anhduc.mevabe.enums.VoucherType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVoucherRequest {

     VoucherType type;
     BigDecimal value;
     BigDecimal minOrderValue;
     int maxUses;
     LocalDateTime expiryDate;
}
