package com.panda.blogapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {
	
	@NotNull
	private String name;

    @NotBlank
    private String content;

    @NotNull
    private Long blogId;
}
