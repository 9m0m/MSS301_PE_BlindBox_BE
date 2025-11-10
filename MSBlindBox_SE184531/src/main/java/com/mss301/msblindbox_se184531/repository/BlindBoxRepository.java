package com.mss301.msblindbox_se184531.repository;

import com.mss301.msblindbox_se184531.dto.BlindBoxDTO;
import com.mss301.msblindbox_se184531.entity.BlindBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindBoxRepository extends JpaRepository<BlindBox, Integer> {

    @Query("SELECT new com.mss301.msblindbox_se184531.dto.BlindBoxDTO(" +
            "b.blindBoxId, b.name, b.categoryId, c.categoryName, b.brandId, " +
            "b.rarity, b.price, b.releaseDate, b.stock) " +
            "FROM BlindBox b " +
            "LEFT JOIN BlindBoxCategory c ON c.categoryId = b.categoryId " +
            "ORDER BY b.blindBoxId DESC")
    List<BlindBoxDTO> findAllWithCategoryNameOrderByIdDesc();
}
