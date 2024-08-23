package com.anhduc.mevabe.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    private UUID userId = null;
    private String fullName;
    private String address;
    private String phone;
    private String email;
    private BigDecimal point;
    private String paymentMethod;
    private BigDecimal shipFee;
    private BigDecimal totalPrice;
    private String voucherCode;
    private List<OrderDetailRequest> orderDetails;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDetailRequest {
        private UUID productId;
        private int quantity;
    }
}