package com.anhduc.mevabe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductSalesDTO {
    private String productName;
    private Integer quantitySold;
}
