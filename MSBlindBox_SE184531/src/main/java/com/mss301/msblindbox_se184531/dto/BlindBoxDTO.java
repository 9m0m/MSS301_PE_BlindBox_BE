package com.mss301.msblindbox_se184531.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlindBoxDTO {
    private Integer blindBoxId;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String rarity;
    private BigDecimal price;
    private LocalDate releaseDate;
    private Integer stock;
}
