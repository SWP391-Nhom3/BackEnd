package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Report findByOrderIdAndCustomerId(UUID orderId, UUID customerId);
}
