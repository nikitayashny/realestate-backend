package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.services.RealtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/realts")
public class RealtController {

    private final RealtService realtService;

    @GetMapping("")
    public ResponseEntity<?> getRealts() {
        try {
            return ResponseEntity.ok(realtService.getRealts());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addRealt(@ModelAttribute("realt") Realt realt,
                                      @RequestParam("files") MultipartFile[] files) {
        try {
            List<String> imageUrls = realtService.uploadImages(files);
            realtService.addRealt(realt, imageUrls);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRealt(@PathVariable Long id) {
        try {
            realtService.deleteRealt(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
