package com.mss301.msbrand_se184531.controller;

import com.mss301.msbrand_se184531.entity.BlindBox;
import com.mss301.msbrand_se184531.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/blindboxes")
public class InternalBlindBoxController {

    @Autowired
    private BrandService brandService;

    @PostMapping
    public ResponseEntity<?> addBlindBox(@RequestBody BlindBox blindBox) {
        try {
            brandService.addBlindBox(blindBox);
            return ResponseEntity.ok("BlindBox added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<BlindBox> list = brandService.getAllBlindBoxes();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlindBox(@PathVariable Integer id, @RequestBody BlindBox blindBox) {
        try {
            blindBox.setBlindBoxId(id);
            brandService.updateBlindBox(blindBox);
            return ResponseEntity.ok("BlindBox updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlindBox(@PathVariable Integer id) {
        try {
            brandService.deleteBlindBox(id);
            return ResponseEntity.ok("BlindBox deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
