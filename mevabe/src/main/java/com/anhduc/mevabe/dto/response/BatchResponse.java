package com.anhduc.mevabe.dto.response;

import com.anhduc.mevabe.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchResponse {
    UUID id;
    String batchNumber;
    Date manufactureDate;
    Date expiryDate;
    Long quantity;
    Integer sold=0;
    private LocalDateTime createdAt;
    Product product;
}