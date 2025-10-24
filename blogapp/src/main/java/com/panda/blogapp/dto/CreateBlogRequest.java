package com.panda.blogapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBlogRequest {

    @NotBlank
    private String title;

    private String subTitle;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    private String image;
    
    private boolean published;
}
