package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Report;
import com.anhduc.mevabe.enums.ReportActionType;
import com.anhduc.mevabe.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Report findByOrderIdAndCustomerId(UUID orderId, UUID customerId);
    List<Report> findByCustomerId(UUID customerId);
    List<Report> findByStatus(ReportStatus status);
    List<Report> findByActionType(ReportActionType actionType);
}
