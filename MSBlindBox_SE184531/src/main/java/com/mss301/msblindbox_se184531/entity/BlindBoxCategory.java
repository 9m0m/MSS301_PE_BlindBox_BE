package com.mss301.msblindbox_se184531.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BlindBoxCategories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlindBoxCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Integer categoryId;

    @Column(name = "CategoryName", length = 255)
    private String categoryName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "RarityLevel", length = 50)
    private String rarityLevel;

    @Column(name = "PriceRange", length = 100)
    private String priceRange;
}
