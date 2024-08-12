package com.anhduc.mevabe.dto.request;

import com.anhduc.mevabe.entity.Brand;
import com.anhduc.mevabe.entity.Category;
import com.anhduc.mevabe.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {
    String name;
    String description;
    BigDecimal price;
    Brand brand;
    Category category;
    @JsonIgnore
    User user;
    @JsonIgnore
    List<String> imageUrl;
    @JsonIgnore
    String coverImageUrl;
    List<MultipartFile> files;
}