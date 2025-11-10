package com.mss301.msbrand_se184531.service;

import com.mss301.msbrand_se184531.entity.Brand;
import com.mss301.msbrand_se184531.entity.BlindBox;

import java.util.Optional;
import java.util.List;

public interface BrandService {
    Optional<Brand> getBrandById(Integer id);

    void addBlindBox(BlindBox blindBox);

    void updateBlindBox(BlindBox blindBox);

    void deleteBlindBox(Integer id);

    List<BlindBox> getAllBlindBoxes();
}
