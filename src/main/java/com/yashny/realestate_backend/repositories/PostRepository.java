package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}