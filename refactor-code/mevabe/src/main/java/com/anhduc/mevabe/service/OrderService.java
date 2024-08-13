package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateOrderRequest;
import com.anhduc.mevabe.dto.request.OrderItemRequest;
import com.anhduc.mevabe.dto.response.OrderResponse;
import com.anhduc.mevabe.entity.*;
import com.anhduc.mevabe.enums.OrderStatus;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class OrderService {


    OrderRepository orderRepository;
    ProductRepository productRepository;
    BatchRepository batchRepository;
    OrderItemRepository orderItemRepository;
    OrderItemBatchRepository orderItemBatchRepository;
    ModelMapper modelMapper;
    VoucherRepository voucherRepository;

    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProduct().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItem = orderItemRepository.save(orderItem);

            totalPrice = totalPrice.add(orderItem.getPrice());

            // Xử lý các lô hàng (batches) theo FIFO
            List<Batch> availableBatches = batchRepository.findByProductIdAndExpiryDateAfterOrderByManufactureDateAsc(
                    product.getId(), new Date());

            int remainingQuantity = itemRequest.getQuantity();

            for (Batch batch : availableBatches) {
                if (remainingQuantity <= 0) {
                    break;
                }

                if (batch.getQuantity() >= remainingQuantity) {
                    createOrderItemBatch(orderItem, batch, remainingQuantity);
                    batch.setQuantity(batch.getQuantity() - remainingQuantity);
                    batchRepository.save(batch);
                    remainingQuantity = 0;
                } else {
                    createOrderItemBatch(orderItem, batch, batch.getQuantity());
                    remainingQuantity -= batch.getQuantity();
                    batch.setQuantity(0);
                    batchRepository.save(batch);
                }
            }

            if (remainingQuantity > 0) {
                throw new IllegalArgumentException("Not enough stock in batches for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        return convertOrderToResponse(order);
    }

    private OrderResponse convertOrderToResponse(Order order) {
        return modelMapper.map(order, OrderResponse.class);
    }

    private void createOrderItemBatch(OrderItem orderItem, Batch batch, int quantity) {
        OrderItemBatch orderItemBatch = new OrderItemBatch();
        orderItemBatch.setOrderItem(orderItem);
        orderItemBatch.setBatch(batch);
        orderItemBatch.setQuantity(quantity);
        orderItemBatchRepository.save(orderItemBatch);
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order cannot be canceled as it is already " + order.getStatus());
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            List<OrderItemBatch> orderItemBatches = orderItemBatchRepository.findByOrderItemId(orderItem.getId());
            for (OrderItemBatch orderItemBatch : orderItemBatches) {
                Batch batch = orderItemBatch.getBatch();
                batch.setQuantity(batch.getQuantity() + orderItemBatch.getQuantity());
                batchRepository.save(batch);
            }

            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }


}
