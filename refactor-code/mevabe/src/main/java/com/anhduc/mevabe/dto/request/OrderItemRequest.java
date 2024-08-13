package com.anhduc.mevabe.dto.request;

import com.anhduc.mevabe.entity.Order;
import com.anhduc.mevabe.entity.OrderItem;
import com.anhduc.mevabe.entity.Product;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
    int quantity;
    BigDecimal price;
    Product product;
    Order order;
}