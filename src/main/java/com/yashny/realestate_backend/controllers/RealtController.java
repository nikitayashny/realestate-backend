package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.dto.RealtsResponse;
import com.yashny.realestate_backend.dto.RequestRealtDto;
import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.services.RealtService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/realts")
public class RealtController {

    private final RealtService realtService;
    private final JwtUtil jwtUtil;

    @GetMapping("")
    public ResponseEntity<?> getRealts(@ModelAttribute RequestRealtDto requestRealtDto) {
        try {
            List<Realt> realts = realtService.getRealts(requestRealtDto);
            long count = realtService.getCount();

            RealtsResponse response = new RealtsResponse(realts, count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRealt(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(realtService.getRealt(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addRealt(@ModelAttribute("realt") Realt realt,
                                      @RequestParam("files") MultipartFile[] files,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            String token = authorization.substring(7);
            User user = jwtUtil.getUserFromToken(token);
            realt.setUser(user);
            List<String> imageUrls = realtService.uploadImages(files);
            realtService.addRealt(realt, imageUrls);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRealt(@PathVariable Long id,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            String token = authorization.substring(7);
            User user = jwtUtil.getUserFromToken(token);
            if (realtService.deleteRealt(id, user))
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likeRealt(@PathVariable Long id) {
        try {
            realtService.likeRealt(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/view/{id}")
    public ResponseEntity<?> viewRealt(@PathVariable Long id) {
        try {
            realtService.viewRealt(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/repost/{id}")
    public ResponseEntity<?> repostRealt(@PathVariable Long id) {
        try {
            realtService.repostRealt(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
