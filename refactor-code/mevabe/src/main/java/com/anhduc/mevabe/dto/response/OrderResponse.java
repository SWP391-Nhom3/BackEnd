package com.anhduc.mevabe.dto.response;

import com.anhduc.mevabe.entity.OrderItem;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse{

    UUID id;
    BigDecimal totalPrice;
    OrderStatus status;
    User user;
    List<OrderItem> orderItems;

}
