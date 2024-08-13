package com.anhduc.mevabe.entity;

import com.anhduc.mevabe.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order extends AuditAble{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    @ManyToOne
    User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

    @OneToOne
    @JoinColumn(name = "voucher_id", referencedColumnName = "id")
    Voucher voucher;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    Transaction transaction;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
