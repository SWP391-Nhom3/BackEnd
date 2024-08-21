package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateReportRequest;
import com.anhduc.mevabe.dto.request.ProcessReportRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.entity.Report;
import com.anhduc.mevabe.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
    ReportService reportService;

    @PostMapping
    ApiResponse<Report> createReport (CreateReportRequest request){
        reportService.create(request);
        return ApiResponse.<Report>builder()
                .message("create report successfully").build();
    }

    @PutMapping("/{reportid}")
    ApiResponse<Report> updateReport (@PathVariable UUID reportid, ProcessReportRequest request){
        reportService.processReport(reportid, request);
        return ApiResponse.<Report>builder().data(reportService.processReport(reportid,request))
                .message("update report successfully").build();
    }

    @GetMapping
    ApiResponse<List<Report>> getAllReports(){
        return ApiResponse.<List<Report>>builder().data(reportService.getAll()).build();
    }

    @GetMapping("/{reportid}")
    ApiResponse<Report> getReport (@PathVariable UUID reportid){
        return ApiResponse.<Report>builder().data(reportService.getById(reportid)).build();
    }

    @GetMapping("/{orderid}/{customerid}")
    ApiResponse<Report> getReportByOrderAndMember (@PathVariable UUID orderid, @PathVariable UUID customerid){
        Report report = reportService.getReport(orderid, customerid);
        return ApiResponse.<Report>builder().data(report).build();
    }

}
