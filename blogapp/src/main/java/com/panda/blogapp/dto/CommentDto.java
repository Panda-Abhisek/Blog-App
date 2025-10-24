
package com.panda.blogapp.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDto {

    private Long id;
    private String name;
    private String content;
    private boolean isApproved;
    private LocalDateTime createdAt;
    private String blogTitle;
}
