package com.anhduc.mevabe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
public class Product extends AuditAble{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(unique = true, nullable = false)
    String name;
    @Size(max = 200000)
    @Column(nullable = false)
    String description;
    @Column(nullable = false)
    BigDecimal price;
    int stockQuantity = 0;
    @Builder.Default
    boolean isActive = false;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;
    String coverImageUrl;
    @ManyToOne
    Brand brand;
    @ManyToOne
    Category category;
    @JsonIgnore
    @ManyToOne
    User user;
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<Article> articles = new ArrayList<>();

}
