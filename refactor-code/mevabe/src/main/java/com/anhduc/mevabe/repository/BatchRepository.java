package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {
    int countBymanufactureDate(Date manufactureDate);
}
