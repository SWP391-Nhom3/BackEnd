package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateReportRequest;
import com.anhduc.mevabe.dto.request.ProcessReportRequest;
import com.anhduc.mevabe.entity.Order;
import com.anhduc.mevabe.entity.Report;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.enums.ReportActionType;
import com.anhduc.mevabe.enums.ReportStatus;
import com.anhduc.mevabe.repository.OrderRepository;
import com.anhduc.mevabe.repository.ReportRepository;
import com.anhduc.mevabe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportService {
    ReportRepository reportRepository;
    OrderRepository orderRepository;
    UserRepository userRepository;

    public Report create (CreateReportRequest request){
        log.info("Received report request from frontend: " + request);
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        String reason = request.getReason();

        Report report = Report.builder()
                        .order(order)
                        .customer(customer)
                        .status(ReportStatus.PENDING)
                        .actionType(ReportActionType.UNKNOWN)
                        .reason(reason)
                        .build();

        return reportRepository.save(report);
    }

    public Report processReport(UUID reportId, ProcessReportRequest request){
        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStaff(staff);
        report.setStatus(ReportStatus.COMPLETED);
        report.setNote(request.getNote());
        report.setActionType(request.getActionType());

        return reportRepository.save(report);
    }

    public List<Report> getAll(){
        return reportRepository.findAll();
    }

    public Report getById(UUID id){
        return reportRepository.findById(id).orElse(null);
    }

    public Report getReport(UUID orderid, UUID memberid){
        if (orderid == null || memberid == null) {
            throw new IllegalArgumentException("Order ID and User ID must not be null");
        }
        return reportRepository.findByOrderIdAndCustomerId(orderid, memberid);
    }
}
