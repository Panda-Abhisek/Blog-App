package com.panda.blogapp.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BlogDto {

    private Long id;
    private String title;
    private String subTitle;
    private String description;
    private String category;
    private String image;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
}
