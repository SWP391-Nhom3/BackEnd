package com.anhduc.mevabe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orderstatus")
public class OrderStatus{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(unique = true, nullable = false)
    String name;

    @OneToMany(mappedBy = "orderStatus")
    private List<Order> orders;

    public OrderStatus(String name){this.name = name;}
}