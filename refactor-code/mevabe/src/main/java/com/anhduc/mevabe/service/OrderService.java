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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    VoucherRepository voucherRepository;

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
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime requiredDate = now.toLocalDateTime();
        Voucher voucher = null;
        if (createOrderRequest.getVoucherCode() != null && !createOrderRequest.getVoucherCode().isEmpty()) {
            voucher = voucherRepository.findVoucherByCode(createOrderRequest.getVoucherCode());
            if (voucher.getExpiryDate().isBefore(now.toLocalDateTime())) {
                throw new RuntimeException("Voucher has expired");
            }
            voucher.setMaxUses(voucher.getMaxUses() - 1);
            voucherRepository.save(voucher);
        }

        Order order = Order.builder()
                .fullName(createOrderRequest.getFullName())
                .address(createOrderRequest.getAddress())
                .phone(createOrderRequest.getPhone())
                .email(createOrderRequest.getEmail())
                .paymentMethod(createOrderRequest.getPaymentMethod())
                .requiredDate(requiredDate)
                .isPreOrder(false)
                .voucher(voucher)
                .shipFee(createOrderRequest.getShipFee())
                .totalPrice(createOrderRequest.getTotalPrice())
                .voucherCode(createOrderRequest.getVoucherCode())
                .orderStatus(status)
                .build();

        if (createOrderRequest.getUserId() != null) {
            User user = userRepository.findById(createOrderRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setMember(user);
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
                            .order(order)
                            .product(product)
                            .productBatch(batch)
                            .quantity(availableInBatch)
                            .build();
                    orderDetails.add(orderDetail);
                    batch.setSold(batch.getSold() + availableInBatch);
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

    public Order createPreOrder(PreOrderRequest preOrderRequest) {
        OrderStatus preOrderStatus = orderStatusRepository.findByName("Đặt trước")
                .orElseThrow(() -> new RuntimeException("Order status 'Đặt trước' not found"));

        Order order = Order.builder()
                .fullName(preOrderRequest.getFullName())
                .address(preOrderRequest.getAddress())
                .phone(preOrderRequest.getPhone())
                .email(preOrderRequest.getEmail())
                .paymentMethod(preOrderRequest.getPaymentMethod())
                .shipFee(preOrderRequest.getShipFee())
                .totalPrice(preOrderRequest.getTotalPrice())
                .voucherCode(preOrderRequest.getVoucherCode())
                .orderStatus(preOrderStatus)
                .isPreOrder(true)
                .build();

        if (preOrderRequest.getUserId() != null) {
            User user = userRepository.findById(preOrderRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setMember(user);
        }

        List<PreOrderDetail> orderDetails = new ArrayList<>();

        for (PreOrderRequest.PreOrderDetailRequest detailRequest : preOrderRequest.getOrderDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            PreOrderDetail orderDetail = PreOrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(detailRequest.getQuantity())
                    .build();

            orderDetails.add(orderDetail);
        }

        order.setPreOrderDetail(orderDetails);
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    public void processPreOrderToOrder() {
        try {
            OrderStatus preOrderStatus = orderStatusRepository.findByName("Đặt trước")
                    .orElseThrow(() -> new RuntimeException("Order status 'Đặt trước' not found"));

            OrderStatus confirmStatus = orderStatusRepository.findByName("Chờ xác nhận")
                    .orElseThrow(() -> new RuntimeException("Order status 'Chờ xác nhận' not found"));

            // Lấy tất cả các đơn hàng "Đặt trước"
            List<Order> preOrders = orderRepository.findByOrderStatus(preOrderStatus)
                    .stream()
                    .sorted(Comparator.comparing(Order::getRequiredDate))
                    .toList();

            for (Order preOrder : preOrders) {
                boolean canFulfillOrder = true;
                List<OrderDetail> orderDetails = new ArrayList<>();

                for (PreOrderDetail preOrderDetail : preOrder.getPreOrderDetail()) {
                    Product product = preOrderDetail.getProduct();
                    int remainingQuantity = preOrderDetail.getQuantity();
                    List<Batch> productBatches = batchRepository.findByProductIdOrderByExpiryDateAsc(product.getId());

                    int totalAvailableQuantity = productBatches.stream()
                            .mapToInt(batch -> batch.getQuantity() - batch.getSold())
                            .sum();

                    if (totalAvailableQuantity < remainingQuantity) {
                        canFulfillOrder = false;
                        break;
                    }

                    for (Batch batch : productBatches) {
                        int availableInBatch = batch.getQuantity() - batch.getSold();

                        if (availableInBatch >= remainingQuantity) {
                            OrderDetail orderDetail = OrderDetail.builder()
                                    .order(preOrder)
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
                                    .order(preOrder)
                                    .product(product)
                                    .productBatch(batch)
                                    .quantity(availableInBatch)
                                    .build();

                            orderDetails.add(orderDetail);
                            batch.setSold(batch.getSold() + availableInBatch);
                            batchRepository.save(batch);
                            remainingQuantity -= availableInBatch;
                        }
                    }
                }

                if (canFulfillOrder) {
                    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
                    LocalDateTime requiredDate = now.toLocalDateTime();

                    preOrder.setOrderStatus(confirmStatus);
                    preOrder.setPreOrder(false);
                    preOrder.setRequiredDate(requiredDate);
                    preOrder.getOrderDetails().clear();
                    preOrder.getOrderDetails().addAll(orderDetails);
                    orderRepository.save(preOrder);
                    for (OrderDetail detail : preOrder.getOrderDetails()) {
                        Product product = detail.getProduct();
                        int totalStockQuantity = batchRepository.findByProductIdOrderByExpiryDateAsc(product.getId())
                                .stream()
                                .mapToInt(b -> b.getQuantity() - b.getSold())
                                .sum();
                        product.setStockQuantity(totalStockQuantity);
                        productRepository.save(product);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while processing pre-orders: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public Order confirmOrder(UUID orderId, UUID shipperId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime requiredDate = now.toLocalDateTime();

        if (!order.getOrderStatus().getName().equalsIgnoreCase("Chờ xác nhận")) {
            throw new RuntimeException("Order cannot be confirmed because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus confirmedStatus = orderStatusRepository.findByName("Đang giao hàng")
                .orElseThrow(() -> new RuntimeException("Order status 'Đang giao hàng' not found"));

        User shipper = userRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));

        order.setOrderStatus(confirmedStatus);
        order.setAcceptedDate(requiredDate);
        order.setShipper(shipper);

        return orderRepository.save(order);
    }

    @Transactional
    public Order shippingOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime requiredDate = now.toLocalDateTime();

        if (!order.getOrderStatus().getName().equalsIgnoreCase("Đang giao hàng")) {
            throw new RuntimeException("Order cannot be confirmed because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus confirmedStatus = orderStatusRepository.findByName("Hoàn thành")
                .orElseThrow(() -> new RuntimeException("Order status 'Đã xác nhận' not found"));
        order.setOrderStatus(confirmedStatus);
        order.setShippedDate(requiredDate);

        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelShippingOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime requiredDate = now.toLocalDateTime();

        if (!order.getOrderStatus().getName().equalsIgnoreCase("Đang giao hàng")) {
            throw new RuntimeException("Order cannot be confirmed because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus confirmedStatus = orderStatusRepository.findByName("Giao hàng không thành công")
                .orElseThrow(() -> new RuntimeException("Order status 'Đã xác nhận' not found"));

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Batch batch = orderDetail.getProductBatch();
            Product product = orderDetail.getProduct();
            batch.setSold(batch.getSold() - orderDetail.getQuantity());
            productBatchRepository.save(batch);
            int totalStockQuantity = productBatchRepository.findByProductIdOrderByExpiryDateAsc(product.getId()).stream()
                    .mapToInt(b -> b.getQuantity() - b.getSold())
                    .sum();
            product.setStockQuantity(totalStockQuantity);
            productRepository.save(product);
        }

        order.setOrderStatus(confirmedStatus);
        order.setShippedDate(requiredDate);

        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime requiredDate = now.toLocalDateTime();

        if (!order.getOrderStatus().getName().equalsIgnoreCase("Chờ xác nhận")) {
            throw new RuntimeException("Order cannot be canceled because it is not in 'Chờ xác nhận' status.");
        }

        OrderStatus canceledStatus = orderStatusRepository.findByName("Đã hủy")
                .orElseThrow(() -> new RuntimeException("Order status 'Đã hủy' not found"));

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Batch batch = orderDetail.getProductBatch();
            Product product = orderDetail.getProduct();
            batch.setSold(batch.getSold() - orderDetail.getQuantity());
            productBatchRepository.save(batch);
            int totalStockQuantity = productBatchRepository.findByProductIdOrderByExpiryDateAsc(product.getId()).stream()
                    .mapToInt(b -> b.getQuantity() - b.getSold())
                    .sum();

            product.setStockQuantity(totalStockQuantity);
            productRepository.save(product);
        }

        order.setOrderStatus(canceledStatus);
        order.setAcceptedDate(requiredDate);
        return orderRepository.save(order);
    }

}
