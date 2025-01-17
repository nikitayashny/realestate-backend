package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.Post;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImageService imageService;

    public List<Post> getPosts() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            post.setComments(post.getComments().stream()
                    .map(comment -> {
                        User userWithoutPassword = comment.getUser();
                        userWithoutPassword.setPassword(null);
                        comment.setPost(null);
                        comment.setUser(userWithoutPassword);
                        return comment;
                    })
                    .collect(Collectors.toList()));
        }
        return posts;
    }

    public void addPost(Post post, String imageUrl) {
        post.setImage(imageUrl);
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
