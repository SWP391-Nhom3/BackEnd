package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateOrderRequest;
import com.anhduc.mevabe.dto.request.OrderItemRequest;
import com.anhduc.mevabe.dto.response.OrderResponse;
import com.anhduc.mevabe.entity.*;
import com.anhduc.mevabe.enums.OrderStatus;
import com.anhduc.mevabe.enums.VoucherStatus;
import com.anhduc.mevabe.enums.VoucherType;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.exception.InsufficientStockException;
import com.anhduc.mevabe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    UserRepository userRepository;

    public OrderResponse create(CreateOrderRequest request) {

        var contex = SecurityContextHolder.getContext();
        String email = contex.getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // add order
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);
        order = orderRepository.save(order);


        // handle order item
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
            orderItem.setPrice(itemRequest.getPrice());

            // handle FIFO for batch
            List<Batch> batches = batchRepository.findAvailableBatchesForProduct(itemRequest.getProduct().getId());
            allocateBatchToOrderItem(orderItem, batches);

            order.addOrderItem(orderItem);
        }

        // apply voucher
        if (request.getVoucherCode() != null) {
            Voucher voucher = voucherRepository.findVoucherByCode(request.getVoucherCode());
            applyVoucher(order, voucher);
        }

        // sum price order
        BigDecimal totalPrice = calculateTotalPrice(order);
        order.setTotalPrice(totalPrice);

        updateUserRewardPoints(user.getId(), totalPrice);

        order.setStatus(OrderStatus.COMPLETED);
        return convertOrderToResponse(order);
    }

    private OrderResponse convertOrderToResponse(Order order) {
        return modelMapper.map(order, OrderResponse.class);
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void applyVoucher(Order order, Voucher voucher)  {
        if (voucher == null || !isValidVoucherForOrder(voucher, order)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        BigDecimal discount = calculateDiscount(voucher, order.getTotalPrice());
        order.setTotalPrice(order.getTotalPrice().subtract(discount));
        voucher.setCurrentUses(voucher.getCurrentUses() + 1);
        voucherRepository.save(voucher);
    }

    private void allocateBatchToOrderItem(OrderItem orderItem, List<Batch> batches) throws InsufficientStockException {
        int remainingQuantity = orderItem.getQuantity();
        for (Batch batch : batches) {
            if (remainingQuantity <= 0) break;
            int allocatedQuantity = Math.min(batch.getQuantity(), remainingQuantity);
            remainingQuantity -= allocatedQuantity;
            orderItem.addBatch(batch, allocatedQuantity);

            // Update batch quantity
            batch.setQuantity(batch.getQuantity() - allocatedQuantity);
            batchRepository.save(batch);
        }

        if (remainingQuantity > 0) {
            throw new InsufficientStockException("Not enough stock for product " + orderItem.getProduct().getName());
        }
    }


    private boolean isValidVoucherForOrder(Voucher voucher, Order order) {
        if (voucher.getExpiryDate().isBefore(LocalDateTime.now()) || voucher.getStatus() == VoucherStatus.EXPIRED) {
            return false;
        }
        if (voucher.getCurrentUses() >= voucher.getMaxUses()) {
            return false;
        }
        if (voucher.getMinOrderValue() != null && order.getTotalPrice().compareTo(voucher.getMinOrderValue()) < 0) {
            return false;
        }
//        if (voucher.getProductId() != null) {
//            boolean productExists = order.getOrderItems().stream()
//                    .anyMatch(item -> item.getProductId().equals(voucher.getProductId()));
//            return productExists;
//        }
        return true;
    }

    private BigDecimal calculateTotalPrice(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscount(Voucher voucher, BigDecimal totalPrice) {
        if (voucher.getVoucherType() == VoucherType.FIXED_AMOUNT) {
            return voucher.getValue();
        } else if (voucher.getVoucherType() == VoucherType.PERCENTAGE) {
            return totalPrice.multiply(voucher.getValue().divide(new BigDecimal(100)));
        }
        return BigDecimal.ZERO;
    }

    private void updateUserRewardPoints(UUID userId, BigDecimal totalPrice) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        int rewardPoints = calculateRewardPoints(totalPrice);
        user.setRewardPoints(user.getRewardPoints() + rewardPoints);
        userRepository.save(user);
    }

    private int calculateRewardPoints(BigDecimal totalPrice) {
        // Giả sử 1 điểm thưởng cho mỗi 10 đơn vị tiền tệ
        return totalPrice.divide(new BigDecimal(10)).intValue();
    }


}
