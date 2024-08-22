package com.anhduc.mevabe.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    LocalDateTime requiredDate;

    LocalDateTime acceptedDate;

    LocalDateTime shippedDate;

    BigDecimal shipFee;

    BigDecimal totalPrice;

    boolean isPreOrder = false;

    @OneToOne
    @JoinColumn(name = "voucher_id", referencedColumnName = "id", nullable = true)
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "statusId")
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreOrderDetail> preOrderDetail = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User member;

    @ManyToOne
    @JoinColumn(name = "shipper_id", referencedColumnName = "id")
    private User shipper;

    @JsonProperty("createdAt")
    public LocalDateTime getCreatedAt() {
        return super.createdAt;
    }

    @JsonProperty("updatedAt")
    public LocalDateTime getUpdatedAt() {
        return super.updatedAt;
    }


}
