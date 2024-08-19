package com.anhduc.mevabe.dto.request;

import com.anhduc.mevabe.entity.Brand;
import com.anhduc.mevabe.entity.Category;
import com.anhduc.mevabe.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBatchRequest {
    Date manufactureDate;
    Date expiryDate;
    Long quantity;
    Product product;
}