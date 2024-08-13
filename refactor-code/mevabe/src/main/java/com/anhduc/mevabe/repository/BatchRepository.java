package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {
    int countByManufactureDate(Date manufactureDate);
    @Query("SELECT b FROM Batch b WHERE b.product.id = :productId AND b.quantity > 0 ORDER BY b.manufactureDate ASC")
    List<Batch> findAvailableBatchesForProduct(@Param("productId") UUID productId);
}
