package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateBatchRequest;
import com.anhduc.mevabe.dto.response.BatchResponse;
import com.anhduc.mevabe.entity.Batch;
import com.anhduc.mevabe.entity.Product;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.repository.BatchRepository;
import com.anhduc.mevabe.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class BatchService {

    BatchRepository batchRepository;
    ModelMapper modelMapper;
    ProductRepository productRepository;
    OrderService orderService;

    public void create(CreateBatchRequest request)  {
        Product product = productRepository.findById(request.getProduct().getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        Batch batch = modelMapper.map(request, Batch.class);
        int counter = batchRepository.countByManufactureDate(batch.getManufactureDate()) + 1;
        batch.generateBatchNumber(counter);
        product.setStockQuantity((int) (product.getStockQuantity() + request.getQuantity()));

        batchRepository.save(batch);
        orderService.processPreOrderToOrder();
    }

    public List<BatchResponse> findAll() {
        return batchRepository.findAll().stream()
                .map(this::convert)
                .toList();
    }

    public BatchResponse findById(UUID id) {
        return convert(batchRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)
        ));
    }

    public BatchResponse update(UUID id, CreateBatchRequest request) {
        Batch batch = batchRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        modelMapper.map(request, batch);
        return convert(batchRepository.save(batch));

    }

    public void delete(UUID id) {
        batchRepository.deleteById(id);
    }

    private BatchResponse convert(Batch batch) {
        return modelMapper.map(batch, BatchResponse.class);
    }
}
