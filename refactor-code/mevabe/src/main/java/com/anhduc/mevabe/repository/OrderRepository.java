package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Order;
import com.anhduc.mevabe.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByOrderStatus(OrderStatus orderStatus);
}
