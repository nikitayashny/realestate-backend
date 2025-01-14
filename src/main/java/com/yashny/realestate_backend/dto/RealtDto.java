package com.yashny.realestate_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class RealtDto {
    private String name;
    private List<String> images;
}
