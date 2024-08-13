package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {
    int countByManufactureDate(Date manufactureDate);
    List<Batch> findByProductIdAndExpiryDateAfterOrderByManufactureDateAsc(UUID productId, Date expiryDate);
}
