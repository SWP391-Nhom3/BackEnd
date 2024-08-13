package com.anhduc.mevabe.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order extends AuditAble{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID Id;
//    Long memId;
//    Long staffId;
    String voucherCode;
    String fullName;
    String address;
    String phone;
    String email;
    String paymentMethod;

    @Temporal(TemporalType.DATE)
    Date requiredDate;

    @Temporal(TemporalType.DATE)
    Date acceptedDate;

    @Temporal(TemporalType.DATE)
    Date shippedDate;

    BigDecimal shipFee;
    BigDecimal totalPrice;

    @OneToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "statusId", insertable = false, updatable = false)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

}
