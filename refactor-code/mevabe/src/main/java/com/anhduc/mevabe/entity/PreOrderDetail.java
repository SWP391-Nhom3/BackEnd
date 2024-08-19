package com.anhduc.mevabe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "preOrderDetail")
public class PreOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID ordDetailId;

    @Column(nullable = false)
    Integer quantity;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false) // Bỏ insertable và updatable
    @JsonIgnore
    private Order order;


    @ManyToOne
    @JoinColumn(name = "productId", nullable = false) // Bỏ insertable và updatable
    private Product product;
}
