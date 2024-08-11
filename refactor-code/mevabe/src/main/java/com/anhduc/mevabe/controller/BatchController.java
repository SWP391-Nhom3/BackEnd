package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateBatchRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.BatchResponse;
import com.anhduc.mevabe.service.BatchService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BatchController {

    BatchService batchService;

    @PostMapping
    ApiResponse<Void> add(@RequestBody @Valid CreateBatchRequest request) throws IOException {
        batchService.create(request);
        return ApiResponse.<Void>builder()
                .message("Batch created successfully")
                .build();
    }


    @GetMapping
    ApiResponse<List<BatchResponse>> getAll() {
        return ApiResponse.<List<BatchResponse>>builder()
                .data(batchService.findAll()).build();
    }

    @GetMapping("/{id}")
    ApiResponse<BatchResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<BatchResponse>builder()
                .data(batchService.findById(id)).build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        batchService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Batch deleted successfully")
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<BatchResponse> update(@RequestBody @Valid CreateBatchRequest request, @PathVariable UUID id) {
        return ApiResponse.<BatchResponse>builder()
                .data(batchService.update(id, request)).build();
    }
}
