package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.dto.CommentDto;
import com.yashny.realestate_backend.entities.Comment;
import com.yashny.realestate_backend.entities.Post;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.CommentRepository;
import com.yashny.realestate_backend.repositories.PostRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public void addComment(CommentDto commentDto, User user) {
        Comment comment = new Comment();
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setUser(user);
        comment.setPost(post);
        comment.setText(commentDto.getText());
        commentRepository.save(comment);
    }

    @Transactional
    public boolean deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getUser() == user || Objects.equals(user.getRole(), "ADMIN") || Objects.equals(user.getRole(), "SUPER_ADMIN")) {
            Post post = comment.getPost();
            post.getComments().remove(comment);
            commentRepository.delete(comment);
            return true;
        }

        return false;
    }
}