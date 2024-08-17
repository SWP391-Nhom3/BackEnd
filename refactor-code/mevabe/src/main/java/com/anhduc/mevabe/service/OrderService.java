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
import java.time.LocalDateTime;
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
    UserRepository userRepository;

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
    @Autowired
    private BatchRepository batchRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        OrderStatus status = orderStatusRepository.findByName("Chờ xác nhận")
                .orElseThrow(() -> new RuntimeException("Order status 'Chờ xác nhận' not found"));
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .fullName(createOrderRequest.getFullName())
                .address(createOrderRequest.getAddress())
                .phone(createOrderRequest.getPhone())
                .email(createOrderRequest.getEmail())
                .paymentMethod(createOrderRequest.getPaymentMethod())
                .requiredDate(now)
                .shipFee(createOrderRequest.getShipFee())
                .totalPrice(createOrderRequest.getTotalPrice())
                .voucherCode(createOrderRequest.getVoucherCode())
                .orderStatus(status)
                .build();

        if (createOrderRequest.getUserId() != null) {
            User user = userRepository.findById(createOrderRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setMember(user);  // Gán user vào Order nếu userId tồn tại
        }

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
                            .order(order)
                            .product(product)
                            .productBatch(batch)
                            .quantity(remainingQuantity)
                            .build();

                    orderDetails.add(orderDetail);
                    batch.setSold(batch.getSold() + remainingQuantity);
                    batchRepository.save(batch);
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
                    batch.setSold(batch.getSold() + availableInBatch); // Cập nhật sold
                    batchRepository.save(batch);
                    remainingQuantity -= availableInBatch;
                }
            }

            if (remainingQuantity > 0) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            int totalStockQuantity = productBatches.stream()
                    .mapToInt(b -> b.getQuantity() - b.getSold())
                    .sum();
            product.setStockQuantity(totalStockQuantity);
            productRepository.save(product);
        }

        order.setOrderDetails(orderDetails);
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    @Transactional
    public Order confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        LocalDateTime now = LocalDateTime.now();

        if (!order.getOrderStatus().getName().equalsIgnoreCase("Chờ xác nhận")) {
            throw new RuntimeException("Order cannot be confirmed because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus confirmedStatus = orderStatusRepository.findByName("Đã xác nhận")
                .orElseThrow(() -> new RuntimeException("Order status 'Đã xác nhận' not found"));
        order.setOrderStatus(confirmedStatus);
        order.setAcceptedDate(now);

        return orderRepository.save(order);
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
        LocalDateTime now = LocalDateTime.now();

        if (!order.getOrderStatus().getName().equalsIgnoreCase("Chờ xác nhận")) {
            throw new RuntimeException("Order cannot be canceled because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus canceledStatus = orderStatusRepository.findByName("Đã hủy")
                .orElseThrow(() -> new RuntimeException("Order status 'Đã hủy' not found"));

        // Trả hàng lại về các lô hàng và cập nhật số lượng tồn kho
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Batch batch = orderDetail.getProductBatch();
            Product product = orderDetail.getProduct();

            // Giảm số lượng đã bán của lô hàng (batch)
            batch.setSold(batch.getSold() - orderDetail.getQuantity());

            // Cập nhật lại batch
            productBatchRepository.save(batch);

            // Tính toán lại số lượng tồn kho của sản phẩm
            int totalStockQuantity = productBatchRepository.findByProductIdOrderByExpiryDateAsc(product.getId()).stream()
                    .mapToInt(b -> b.getQuantity() - b.getSold())
                    .sum();

            product.setStockQuantity(totalStockQuantity);
            productRepository.save(product);
        }

        order.setOrderStatus(canceledStatus);
        order.setAcceptedDate(now);
        return orderRepository.save(order);
    }

}
