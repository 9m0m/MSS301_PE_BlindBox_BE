package com.mss301.msbrand_se184531.repository;

import com.mss301.msbrand_se184531.entity.BlindBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindBoxRepository extends JpaRepository<BlindBox, Integer> {
}
