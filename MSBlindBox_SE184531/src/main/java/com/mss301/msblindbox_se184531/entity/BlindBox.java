package com.mss301.msblindbox_se184531.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BlindBoxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlindBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BlindBoxID")
    private Integer blindBoxId;

    @Column(name = "Name", length = 255)
    private String name;

    @Column(name = "CategoryID")
    private Integer categoryId;

    @Column(name = "BrandID")
    private Integer brandId;

    @Column(name = "Rarity", length = 50)
    private String rarity;

    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;

    @Column(name = "Stock")
    private Integer stock;
}
