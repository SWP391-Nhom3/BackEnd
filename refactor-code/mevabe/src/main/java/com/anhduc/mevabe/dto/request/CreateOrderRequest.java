//package com.anhduc.mevabe.dto.request;
//
//import com.anhduc.mevabe.entity.*;
//import com.anhduc.mevabe.enums.OrderStatus;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class CreateOrderRequest {
//    BigDecimal totalPrice;
//    @Enumerated(EnumType.STRING)
//    OrderStatus status;
//    User user;
//    List<OrderItemRequest> orderItems;
//}