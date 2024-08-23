package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    @Query("SELECT SUM(oi.quantity) FROM OrderDetail oi WHERE oi.product.id = :productId")
    Integer findTotalQuantitySoldByProductId(UUID productId);

    @Query("SELECT SUM(oi.quantity) FROM OrderDetail oi")
    Integer findTotalQuantitySold();

}
