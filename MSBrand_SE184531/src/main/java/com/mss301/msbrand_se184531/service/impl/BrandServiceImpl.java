package com.mss301.msbrand_se184531.service.impl;

import com.mss301.msbrand_se184531.entity.Brand;
import com.mss301.msbrand_se184531.entity.BlindBox;
import com.mss301.msbrand_se184531.repository.BrandRepository;
import com.mss301.msbrand_se184531.repository.BlindBoxRepository;
import com.mss301.msbrand_se184531.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BlindBoxRepository blindBoxRepository;

    @Override
    public Optional<Brand> getBrandById(Integer id) {
        return brandRepository.findById(id);
    }

    @Override
    public void addBlindBox(BlindBox blindBox) {
        blindBoxRepository.save(blindBox);
    }

    @Override
    public void updateBlindBox(BlindBox blindBox) {
        blindBoxRepository.save(blindBox);
    }

    @Override
    public void deleteBlindBox(Integer id) {
        blindBoxRepository.deleteById(id);
    }

    @Override
    public List<BlindBox> getAllBlindBoxes() {
        return blindBoxRepository.findAll();
    }
}
