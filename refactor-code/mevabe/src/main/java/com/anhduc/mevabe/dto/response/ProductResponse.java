package com.anhduc.mevabe.dto.response;

import com.anhduc.mevabe.entity.Brand;
import com.anhduc.mevabe.entity.Category;
import com.anhduc.mevabe.entity.User;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    UUID id;
    String name;
    String description;
    BigDecimal price;
    int stockQuantity;
    List<String> imageUrl;
    String coverImageUrl;
    Brand brand;
    Category category;
    User user;
}