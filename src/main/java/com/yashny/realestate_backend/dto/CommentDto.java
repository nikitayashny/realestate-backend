package com.yashny.realestate_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CommentDto {
    private String text;
    private Long postId;
}
