package com.anhduc.mevabe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String batchNumber;
    Date manufactureDate;
    Date expiryDate;
    int quantity;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    Product product;

    public void generateBatchNumber(int counter) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(this.manufactureDate);
        this.batchNumber = String.format("%s-%03d", dateStr, counter);
    }
}
