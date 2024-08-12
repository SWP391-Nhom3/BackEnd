package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.OrderItemBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemBatchRepository extends JpaRepository<OrderItemBatch, UUID> {
    List<OrderItemBatch> findByOrderItemId(UUID id);
}
