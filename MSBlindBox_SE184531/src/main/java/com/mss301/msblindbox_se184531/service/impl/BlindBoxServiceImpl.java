package com.mss301.msblindbox_se184531.service.impl;

import com.mss301.msblindbox_se184531.dto.BlindBoxDTO;
import com.mss301.msblindbox_se184531.entity.BlindBox;
import com.mss301.msblindbox_se184531.repository.BlindBoxRepository;
import com.mss301.msblindbox_se184531.service.BlindBoxService;
import com.mss301.msblindbox_se184531.service.BrandSyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BlindBoxServiceImpl implements BlindBoxService {

    @Autowired
    private BlindBoxRepository blindBoxRepository;

    @Autowired
    private BrandSyncClient brandSyncClient;

    @Override
    public List<BlindBoxDTO> getAllBlindBoxes() {
        return blindBoxRepository.findAllWithCategoryNameOrderByIdDesc();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlindBox addBlindBox(BlindBox blindBox) throws Exception {
        validateBlindBox(blindBox);

        // Ensure create: ignore any client-provided ID to avoid mistaken update
        blindBox.setBlindBoxId(null);

        blindBox.setReleaseDate(LocalDate.now());

        BlindBox saved = blindBoxRepository.save(blindBox);
        try {
            brandSyncClient.createBlindBox(saved);
        } catch (Exception e) {
            throw new Exception("Failed to sync with MSBrand service: " + e.getMessage());
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlindBox updateBlindBox(Integer id, BlindBox blindBox) throws Exception {
        Optional<BlindBox> existingOpt = blindBoxRepository.findById(id);
        if (!existingOpt.isPresent()) {
            throw new Exception("BlindBox not found with id: " + id);
        }

        validateBlindBox(blindBox);

        blindBox.setReleaseDate(LocalDate.now());
        blindBox.setBlindBoxId(id);

        BlindBox updated = blindBoxRepository.save(blindBox);

        try {
            brandSyncClient.updateBlindBox(id, updated);
        } catch (Exception e) {
            throw new Exception("Failed to sync with MSBrand service: " + e.getMessage());
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBlindBox(Integer id) throws Exception {
        Optional<BlindBox> existingOpt = blindBoxRepository.findById(id);
        if (!existingOpt.isPresent()) {
            throw new Exception("BlindBox not found with id: " + id);
        }
        // xóa bên thứ 3 rồi mới xóa service hiện tại tức là call qua syncclient xóa bên
        // kia trước để sync 2 bên
        try {
            brandSyncClient.deleteBlindBox(id);
        } catch (Exception e) {
            throw new Exception("Failed to sync with MSBrand service: " + e.getMessage());
        }
        blindBoxRepository.deleteById(id);
    }

    private void validateBlindBox(BlindBox blindBox) throws Exception {
        // Validate Name: greater than 10 characters
        if (blindBox.getName() == null || blindBox.getName().length() <= 10) {
            throw new Exception("Name must be greater than 10 characters");
        }

        // Validate Stock: between 1 and 100
        if (blindBox.getStock() == null || blindBox.getStock() < 1 || blindBox.getStock() > 100) {
            throw new Exception("Stock must be between 1 and 100");
        }

        // Validate all required fields
        if (blindBox.getCategoryId() == null) {
            throw new Exception("CategoryID is required");
        }
        if (blindBox.getBrandId() == null) {
            throw new Exception("BrandID is required");
        }
        if (blindBox.getRarity() == null || blindBox.getRarity().isEmpty()) {
            throw new Exception("Rarity is required");
        }
        if (blindBox.getPrice() == null) {
            throw new Exception("Price is required");
        }
    }
}
