package com.mss301.msblindbox_se184531.config;

import com.mss301.msblindbox_se184531.entity.BlindBoxCategory;
import com.mss301.msblindbox_se184531.entity.BlindBox;
import com.mss301.msblindbox_se184531.repository.BlindBoxCategoryRepository;
import com.mss301.msblindbox_se184531.repository.BlindBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BlindBoxCategoryRepository categoryRepository;
    private final BlindBoxRepository blindBoxRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize Categories
        if (categoryRepository.count() == 0) {
            System.out.println("Initializing BlindBoxCategories data...");

            BlindBoxCategory cat1 = new BlindBoxCategory();
            cat1.setCategoryName("Fantasy");
            cat1.setDescription("Mystical creatures, wizards, and legendary beings.");
            cat1.setRarityLevel("Common to Ultra Rare");
            cat1.setPriceRange("$10 - $60");
            categoryRepository.save(cat1);

            BlindBoxCategory cat2 = new BlindBoxCategory();
            cat2.setCategoryName("Gaming");
            cat2.setDescription("Blind boxes featuring characters from popular video games.");
            cat2.setRarityLevel("Common to Epic");
            cat2.setPriceRange("$25 - $70");
            categoryRepository.save(cat2);

            BlindBoxCategory cat3 = new BlindBoxCategory();
            cat3.setCategoryName("Sci-Fi");
            cat3.setDescription("Space, futuristic themes, and robotic collectibles.");
            cat3.setRarityLevel("Rare to Legendary");
            cat3.setPriceRange("$30 - $80");
            categoryRepository.save(cat3);

            BlindBoxCategory cat4 = new BlindBoxCategory();
            cat4.setCategoryName("Anime");
            cat4.setDescription("Popular anime characters and themed mystery figures.");
            cat4.setRarityLevel("Common to Legendary");
            cat4.setPriceRange("$15 - $100");
            categoryRepository.save(cat4);

            BlindBoxCategory cat5 = new BlindBoxCategory();
            cat5.setCategoryName("Steampunk");
            cat5.setDescription("Victorian-era inspired mechanical and fantasy figures.");
            cat5.setRarityLevel("Rare to Epic");
            cat5.setPriceRange("$100 - $190");
            categoryRepository.save(cat5);

            System.out.println("✅ Successfully initialized 5 BlindBoxCategories");
        }

        // Initialize BlindBoxes
        if (blindBoxRepository.count() == 0) {
            System.out.println("Initializing BlindBoxes data...");

            BlindBox bb1 = new BlindBox();
            bb1.setName("Mystic Creatures Series 1");
            bb1.setCategoryId(1);
            bb1.setBrandId(1);
            bb1.setRarity("Rare");
            bb1.setPrice(new BigDecimal("29.99"));
            bb1.setReleaseDate(LocalDate.of(2024, 1, 15));
            bb1.setStock(150);
            blindBoxRepository.save(bb1);

            BlindBox bb2 = new BlindBox();
            bb2.setName("Cyberpunk Warriors");
            bb2.setCategoryId(2);
            bb2.setBrandId(2);
            bb2.setRarity("Ultra Rare");
            bb2.setPrice(new BigDecimal("49.99"));
            bb2.setReleaseDate(LocalDate.of(2023, 11, 20));
            bb2.setStock(75);
            blindBoxRepository.save(bb2);

            BlindBox bb3 = new BlindBox();
            bb3.setName("Fantasy Legends");
            bb3.setCategoryId(1);
            bb3.setBrandId(1);
            bb3.setRarity("Common");
            bb3.setPrice(new BigDecimal("19.99"));
            bb3.setReleaseDate(LocalDate.of(2024, 2, 10));
            bb3.setStock(200);
            blindBoxRepository.save(bb3);

            BlindBox bb4 = new BlindBox();
            bb4.setName("Space Explorers");
            bb4.setCategoryId(3);
            bb4.setBrandId(3);
            bb4.setRarity("Epic");
            bb4.setPrice(new BigDecimal("59.99"));
            bb4.setReleaseDate(LocalDate.of(2023, 12, 5));
            bb4.setStock(50);
            blindBoxRepository.save(bb4);

            BlindBox bb5 = new BlindBox();
            bb5.setName("Neon Anime Stars");
            bb5.setCategoryId(4);
            bb5.setBrandId(1);
            bb5.setRarity("Legendary");
            bb5.setPrice(new BigDecimal("99.99"));
            bb5.setReleaseDate(LocalDate.of(2024, 3, 1));
            bb5.setStock(25);
            blindBoxRepository.save(bb5);

            BlindBox bb6 = new BlindBox();
            bb6.setName("Retro Arcade Heroes");
            bb6.setCategoryId(2);
            bb6.setBrandId(2);
            bb6.setRarity("Common");
            bb6.setPrice(new BigDecimal("24.99"));
            bb6.setReleaseDate(LocalDate.of(2024, 1, 30));
            bb6.setStock(180);
            blindBoxRepository.save(bb6);

            BlindBox bb7 = new BlindBox();
            bb7.setName("Mythical Beasts Collection");
            bb7.setCategoryId(1);
            bb7.setBrandId(3);
            bb7.setRarity("Ultra Rare");
            bb7.setPrice(new BigDecimal("54.99"));
            bb7.setReleaseDate(LocalDate.of(2023, 10, 10));
            bb7.setStock(60);
            blindBoxRepository.save(bb7);

            BlindBox bb8 = new BlindBox();
            bb8.setName("Superhero Legends");
            bb8.setCategoryId(3);
            bb8.setBrandId(2);
            bb8.setRarity("Rare");
            bb8.setPrice(new BigDecimal("39.99"));
            bb8.setReleaseDate(LocalDate.of(2024, 2, 20));
            bb8.setStock(120);
            blindBoxRepository.save(bb8);

            BlindBox bb9 = new BlindBox();
            bb9.setName("Steampunk Adventurers");
            bb9.setCategoryId(2);
            bb9.setBrandId(3);
            bb9.setRarity("Epic");
            bb9.setPrice(new BigDecimal("69.99"));
            bb9.setReleaseDate(LocalDate.of(2023, 12, 15));
            bb9.setStock(40);
            blindBoxRepository.save(bb9);

            BlindBox bb10 = new BlindBox();
            bb10.setName("Kawaii Anime Pets");
            bb10.setCategoryId(4);
            bb10.setBrandId(1);
            bb10.setRarity("Common");
            bb10.setPrice(new BigDecimal("14.99"));
            bb10.setReleaseDate(LocalDate.of(2024, 3, 5));
            bb10.setStock(250);
            blindBoxRepository.save(bb10);

            System.out.println("✅ Successfully initialized 10 BlindBoxes");
        }
    }
}
