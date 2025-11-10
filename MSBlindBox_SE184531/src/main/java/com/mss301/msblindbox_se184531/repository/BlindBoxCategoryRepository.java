package com.mss301.msblindbox_se184531.repository;

import com.mss301.msblindbox_se184531.entity.BlindBoxCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindBoxCategoryRepository extends JpaRepository<BlindBoxCategory, Integer> {
}
