package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.response.ProductSalesDTO;
import com.anhduc.mevabe.entity.Product;
import com.anhduc.mevabe.repository.ProductRepository;
import com.anhduc.mevabe.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/total-sold")
    public Integer getTotalQuantitySold() {
        return statisticsService.getTotalQuantitySold();
    }

    @GetMapping("/total-sold/product/{productId}")
    public Integer getTotalQuantitySoldByProduct(UUID productId) {
        return statisticsService.getTotalQuantitySoldByProduct(productId);
    }

    @GetMapping("/total-sold-by-products")
    public List<ProductSalesDTO> getTotalSoldByProducts() {
        List<ProductSalesDTO> productSales = new ArrayList<>();

        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            Integer totalSold = statisticsService.getTotalQuantitySoldByProduct(product.getId());
            productSales.add(new ProductSalesDTO(product.getName(), totalSold != null ? totalSold : 0));
        }

        return productSales;
    }

}
