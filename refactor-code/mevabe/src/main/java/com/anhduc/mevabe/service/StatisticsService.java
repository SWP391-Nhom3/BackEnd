package com.anhduc.mevabe.service;

import com.anhduc.mevabe.repository.OrderDetailRepository;
import com.anhduc.mevabe.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderItemRepository;

    public Integer getTotalQuantitySoldByProduct(UUID productId) {
        return orderItemRepository.findTotalQuantitySoldByProductId(productId);
    }

    public Integer getTotalQuantitySold() {
        return orderItemRepository.findTotalQuantitySold();
    }
}
