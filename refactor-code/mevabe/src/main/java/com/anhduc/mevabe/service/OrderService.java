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
import java.util.*;

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
    ProductRepository productRepository;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        OrderStatus status = orderStatusRepository.findByName("Chờ xác nhận")
                .orElseThrow(() -> new RuntimeException("Order status 'Chờ xác nhận' not found"));

        Order order = Order.builder()
                .fullName(createOrderRequest.getFullName())
                .address(createOrderRequest.getAddress())
                .phone(createOrderRequest.getPhone())
                .email(createOrderRequest.getEmail())
                .paymentMethod(createOrderRequest.getPaymentMethod())
                .requiredDate(createOrderRequest.getRequiredDate())
                .shipFee(createOrderRequest.getShipFee())
                .totalPrice(createOrderRequest.getTotalPrice())
                .voucherCode(createOrderRequest.getVoucherCode())
                .orderStatus(status)
                .build();

        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CreateOrderRequest.OrderDetailRequest detailRequest : createOrderRequest.getOrderDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int remainingQuantity = detailRequest.getQuantity();

            List<Batch> productBatches = productBatchRepository.findByProductIdOrderByExpiryDateAsc(product.getId());

            for (Batch batch : productBatches) {
                int availableInBatch = batch.getQuantity() - batch.getSold();

                if (availableInBatch >= remainingQuantity) {
                    OrderDetail orderDetail = OrderDetail.builder()
                            .order(order)  // Liên kết với Order
                            .product(product)
                            .productBatch(batch)
                            .quantity(remainingQuantity)
                            .build();

                    orderDetails.add(orderDetail);
                    batch.setSold(batch.getSold() + remainingQuantity);

                    remainingQuantity = 0;
                    break;
                } else if (availableInBatch > 0) {
                    OrderDetail orderDetail = OrderDetail.builder()
                            .order(order)  // Liên kết với Order
                            .product(product)
                            .productBatch(batch)
                            .quantity(availableInBatch)
                            .build();

                    orderDetails.add(orderDetail);
                    remainingQuantity -= availableInBatch;
                }
            }

            if (remainingQuantity > 0) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        order.setOrderDetails(orderDetails);
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    @Transactional
    public Order confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra trạng thái hiện tại của đơn hàng
        if (!order.getOrderStatus().getName().equalsIgnoreCase("Chờ xác nhận")) {
            throw new RuntimeException("Order cannot be confirmed because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus confirmedStatus = orderStatusRepository.findByName("Đã xác nhận")
                .orElseThrow(() -> new RuntimeException("Order status 'Đã xác nhận' not found"));
        order.setOrderStatus(confirmedStatus);

        // Cập nhật thông tin về số lượng sản phẩm trong kho
        for (OrderDetail detail : order.getOrderDetails()) {
            Batch batch = detail.getProductBatch();
            batch.setSold(batch.getSold() + detail.getQuantity());
            productBatchRepository.save(batch);
            updateProductStockQuantity(batch.getProduct());
        }

        return orderRepository.save(order);
    }

    private void reserveStock(OrderDetail detail) {
        List<Batch> productBatches = productBatchRepository.findByProductIdAndExpiryDateAfter(
                detail.getProductBatch().getProduct().getId(), new Date());

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
        OrderStatus orderStatus = orderStatusRepository.findByName(statusName)
                .orElseThrow(() -> new RuntimeException("Order status not found"));

        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra trạng thái hiện tại của đơn hàng
        if (!order.getOrderStatus().getName().equalsIgnoreCase("Chờ xác nhận")) {
            throw new RuntimeException("Order cannot be canceled because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus canceledStatus = orderStatusRepository.findByName("Đã hủy")
                .orElseThrow(() -> new RuntimeException("Order status 'Đã hủy' not found"));

        order.setOrderStatus(canceledStatus);
        return orderRepository.save(order);
    }

    private void updateProductStockQuantity(Product product) {
        List<Batch> productBatches = productBatchRepository.findByProductIdOrderByExpiryDateAsc(product.getId());

        int totalQuantity = 0;
        int totalSold = 0;

        for (Batch batch : productBatches) {
            totalQuantity += batch.getQuantity();
            totalSold += batch.getSold();
        }

        int stockQuantity = totalQuantity - totalSold;
        product.setStockQuantity(stockQuantity);

        productRepository.save(product);
    }
}
