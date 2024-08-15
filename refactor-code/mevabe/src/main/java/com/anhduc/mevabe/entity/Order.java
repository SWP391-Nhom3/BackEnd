package com.anhduc.mevabe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order extends AuditAble {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

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
    @JoinColumn(name = "voucher_id", referencedColumnName = "id", nullable = true)
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "statusId")
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
