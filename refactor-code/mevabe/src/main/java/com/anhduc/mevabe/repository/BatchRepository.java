package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Batch;
import com.anhduc.mevabe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {
    int countByManufactureDate(Date manufactureDate);
    List<Batch> findByProductIdAndExpiryDateAfter(UUID productId, Date currentDate);
    List<Batch> findByProductIdOrderByExpiryDateAsc(UUID productId);

}
