package com.anhduc.mevabe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class OrderItem extends AuditAble{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    int quantity;
    BigDecimal price;
    @ManyToOne
    Product product;
    @ManyToOne
    Order order;
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemBatch> batches = new ArrayList<>();

    public void addBatch(Batch batch, int allocatedQuantity) {
        OrderItemBatch orderItemBatch = new OrderItemBatch();
        orderItemBatch.setBatch(batch);
        orderItemBatch.setOrderItem(this);
        orderItemBatch.setQuantity(allocatedQuantity);
        batches.add(orderItemBatch);
    }

}
