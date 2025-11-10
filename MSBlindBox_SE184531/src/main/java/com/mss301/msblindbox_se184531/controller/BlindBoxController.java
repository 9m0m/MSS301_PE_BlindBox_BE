package com.mss301.msblindbox_se184531.controller;

import com.mss301.msblindbox_se184531.dto.BlindBoxDTO;
import com.mss301.msblindbox_se184531.entity.BlindBox;
import com.mss301.msblindbox_se184531.service.BlindBoxService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blindboxes")
public class BlindBoxController {

    @Autowired
    private BlindBoxService blindBoxService;

    @GetMapping
    public ResponseEntity<?> getAllBlindBoxes() {
        try {
            List<BlindBoxDTO> blindBoxes = blindBoxService.getAllBlindBoxes();
            return ResponseEntity.ok(blindBoxes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> addBlindBox(@RequestBody BlindBox blindBox) {
        try {
            BlindBox saved = blindBoxService.addBlindBox(blindBox);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> updateBlindBox(
            @PathVariable Integer id,
            @RequestBody BlindBox blindBox) {
        try {
            BlindBox updated = blindBoxService.updateBlindBox(id, blindBox);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> deleteBlindBox(@PathVariable Integer id) {
        try {
            blindBoxService.deleteBlindBox(id);
            return ResponseEntity.ok("BlindBox deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
