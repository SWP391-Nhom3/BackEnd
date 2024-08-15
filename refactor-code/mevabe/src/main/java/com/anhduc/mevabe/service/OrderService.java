package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.*;
import com.anhduc.mevabe.entity.*;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    BatchRepository productBatchRepository;

    @Autowired
    BatchService productBatchService;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }


    @Transactional
    public Order createOrder(Order order) {
        for (OrderDetail detail : order.getOrderDetails()) {
            UUID productId = detail.getProductBatch().getProduct().getId();
            int totalAvailableQuantity = productBatchService.getTotalAvailableQuantity(productId);
            if (totalAvailableQuantity < detail.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + detail.getProductBatch().getProduct().getName());
            }

            reserveStock(detail);
        }
        return orderRepository.save(order);
    }

    @Transactional
    public void confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        for (OrderDetail detail : order.getOrderDetails()) {
            int remainingQuantity = detail.getQuantity();
            List<Batch> productBatches = productBatchRepository.findByProductIdAndExpiryDateAfter(detail.getProductBatch().getProduct().getId(), new Date());

            for (Batch batch : productBatches) {
                int availableInBatch = batch.getQuantity() - batch.getSold();

                if (availableInBatch >= remainingQuantity) {
                    batch.setSold(batch.getSold() + remainingQuantity);
                    productBatchRepository.save(batch);
                    break;
                } else {
                    batch.setSold(batch.getQuantity());
                    remainingQuantity -= availableInBatch;
                    productBatchRepository.save(batch);
                }
            }

            if (remainingQuantity > 0) {
                throw new RuntimeException("Insufficient stock to confirm the order for product: " + detail.getProductBatch().getProduct().getName());
            }
        }
        order.setOrderStatus(orderStatusRepository.findByName("Đã xác nhận")
                .orElseThrow(() -> new RuntimeException("Order status not found")));

        orderRepository.save(order);
    }

    private void reserveStock(OrderDetail detail) {
        List<Batch> productBatches = productBatchRepository.findByProductIdAndExpiryDateAfter(detail.getProductBatch().getProduct().getId(), new Date());

        int remainingQuantity = detail.getQuantity();
        for (Batch batch : productBatches) {
            int availableInBatch = batch.getQuantity() - batch.getSold();
            if (availableInBatch >= remainingQuantity) {
                batch.setSold(batch.getSold() + remainingQuantity);
                productBatchRepository.save(batch);
                break;
            } else {
                batch.setSold(batch.getQuantity());
                remainingQuantity -= availableInBatch;
                productBatchRepository.save(batch);
            }
        }

        if (remainingQuantity > 0) {
            throw new RuntimeException("Insufficient stock for product: " + detail.getProductBatch().getProduct().getName());
        }
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, String statusName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Fetch and set the new order status
        OrderStatus orderStatus = orderStatusRepository.findByName(statusName)
                .orElseThrow(() -> new RuntimeException("Order status not found"));

        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }
}
