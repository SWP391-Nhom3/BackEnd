package com.anhduc.mevabe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
public class Product extends AuditAble{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(unique = true, nullable = false)
    String name;
    String description;
    @Column(nullable = false)
    BigDecimal price;
    int stockQuantity = 0;
    String imageUrl;
    @ManyToOne
    Brand brand;
    @ManyToOne
    Category category;
    @ManyToOne
    User user;

}
