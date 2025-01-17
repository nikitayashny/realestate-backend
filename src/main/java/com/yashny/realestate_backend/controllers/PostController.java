package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.entities.Post;
import com.yashny.realestate_backend.services.ImageService;
import com.yashny.realestate_backend.services.PostService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;

    @GetMapping("")
    public ResponseEntity<?> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }

    @PostMapping("")
    public ResponseEntity<?> addPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                     @ModelAttribute Post post,
                                     @RequestParam("file") MultipartFile file) {
        String token = authorization.substring(7);
        if (jwtUtil.isAdmin(token) || jwtUtil.isSuperAdmin(token)) {
            try {
                String imageUrl = imageService.uploadImage(file);
                postService.addPost(post, imageUrl);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                        @PathVariable Long id) {
        String token = authorization.substring(7);
        if (jwtUtil.isAdmin(token) || jwtUtil.isSuperAdmin(token)) {
            try {
                postService.deletePost(id);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

}
