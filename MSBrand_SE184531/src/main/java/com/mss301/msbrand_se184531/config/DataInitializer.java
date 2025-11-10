package com.mss301.msbrand_se184531.config;

import com.mss301.msbrand_se184531.entity.Brand;
import com.mss301.msbrand_se184531.entity.BlindBox;
import com.mss301.msbrand_se184531.repository.BrandRepository;
import com.mss301.msbrand_se184531.repository.BlindBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final BlindBoxRepository blindBoxRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize Brands
        if (brandRepository.count() == 0) {
            System.out.println("Initializing Brand data...");

            Brand brand1 = new Brand();
            brand1.setBrandName("POP MART");
            brand1.setCountryOfOrigin("China");
            brandRepository.save(brand1);

            Brand brand2 = new Brand();
            brand2.setBrandName("Funko");
            brand2.setCountryOfOrigin("USA");
            brandRepository.save(brand2);

            Brand brand3 = new Brand();
            brand3.setBrandName("Kidrobot");
            brand3.setCountryOfOrigin("USA");
            brandRepository.save(brand3);

            System.out.println("✅ Successfully initialized 3 Brands");
        }

        // Initialize BlindBoxes (duplicate table for sync)
        if (blindBoxRepository.count() == 0) {
            System.out.println("Initializing BlindBoxes data in MSBrand...");

            BlindBox bb1 = new BlindBox();
            bb1.setBlindBoxId(1);
            bb1.setName("Mystic Creatures Series 1");
            bb1.setCategoryId(1);
            bb1.setBrandId(1);
            bb1.setRarity("Rare");
            bb1.setPrice(new BigDecimal("29.99"));
            bb1.setReleaseDate(LocalDate.of(2024, 1, 15));
            bb1.setStock(150);
            blindBoxRepository.save(bb1);

            BlindBox bb2 = new BlindBox();
            bb2.setBlindBoxId(2);
            bb2.setName("Cyberpunk Warriors");
            bb2.setCategoryId(2);
            bb2.setBrandId(2);
            bb2.setRarity("Ultra Rare");
            bb2.setPrice(new BigDecimal("49.99"));
            bb2.setReleaseDate(LocalDate.of(2023, 11, 20));
            bb2.setStock(75);
            blindBoxRepository.save(bb2);

            BlindBox bb3 = new BlindBox();
            bb3.setBlindBoxId(3);
            bb3.setName("Fantasy Legends");
            bb3.setCategoryId(1);
            bb3.setBrandId(1);
            bb3.setRarity("Common");
            bb3.setPrice(new BigDecimal("19.99"));
            bb3.setReleaseDate(LocalDate.of(2024, 2, 10));
            bb3.setStock(200);
            blindBoxRepository.save(bb3);

            BlindBox bb4 = new BlindBox();
            bb4.setBlindBoxId(4);
            bb4.setName("Space Explorers");
            bb4.setCategoryId(3);
            bb4.setBrandId(3);
            bb4.setRarity("Epic");
            bb4.setPrice(new BigDecimal("59.99"));
            bb4.setReleaseDate(LocalDate.of(2023, 12, 5));
            bb4.setStock(50);
            blindBoxRepository.save(bb4);

            BlindBox bb5 = new BlindBox();
            bb5.setBlindBoxId(5);
            bb5.setName("Neon Anime Stars");
            bb5.setCategoryId(4);
            bb5.setBrandId(1);
            bb5.setRarity("Legendary");
            bb5.setPrice(new BigDecimal("99.99"));
            bb5.setReleaseDate(LocalDate.of(2024, 3, 1));
            bb5.setStock(25);
            blindBoxRepository.save(bb5);

            BlindBox bb6 = new BlindBox();
            bb6.setBlindBoxId(6);
            bb6.setName("Retro Arcade Heroes");
            bb6.setCategoryId(2);
            bb6.setBrandId(2);
            bb6.setRarity("Common");
            bb6.setPrice(new BigDecimal("24.99"));
            bb6.setReleaseDate(LocalDate.of(2024, 1, 30));
            bb6.setStock(180);
            blindBoxRepository.save(bb6);

            BlindBox bb7 = new BlindBox();
            bb7.setBlindBoxId(7);
            bb7.setName("Mythical Beasts Collection");
            bb7.setCategoryId(1);
            bb7.setBrandId(3);
            bb7.setRarity("Ultra Rare");
            bb7.setPrice(new BigDecimal("54.99"));
            bb7.setReleaseDate(LocalDate.of(2023, 10, 10));
            bb7.setStock(60);
            blindBoxRepository.save(bb7);

            BlindBox bb8 = new BlindBox();
            bb8.setBlindBoxId(8);
            bb8.setName("Superhero Legends");
            bb8.setCategoryId(3);
            bb8.setBrandId(2);
            bb8.setRarity("Rare");
            bb8.setPrice(new BigDecimal("39.99"));
            bb8.setReleaseDate(LocalDate.of(2024, 2, 20));
            bb8.setStock(120);
            blindBoxRepository.save(bb8);

            BlindBox bb9 = new BlindBox();
            bb9.setBlindBoxId(9);
            bb9.setName("Steampunk Adventurers");
            bb9.setCategoryId(2);
            bb9.setBrandId(3);
            bb9.setRarity("Epic");
            bb9.setPrice(new BigDecimal("69.99"));
            bb9.setReleaseDate(LocalDate.of(2023, 12, 15));
            bb9.setStock(40);
            blindBoxRepository.save(bb9);

            BlindBox bb10 = new BlindBox();
            bb10.setBlindBoxId(10);
            bb10.setName("Kawaii Anime Pets");
            bb10.setCategoryId(4);
            bb10.setBrandId(1);
            bb10.setRarity("Common");
            bb10.setPrice(new BigDecimal("14.99"));
            bb10.setReleaseDate(LocalDate.of(2024, 3, 5));
            bb10.setStock(250);
            blindBoxRepository.save(bb10);

            System.out.println("✅ Successfully initialized 10 BlindBoxes in MSBrand");
        }
    }
}
