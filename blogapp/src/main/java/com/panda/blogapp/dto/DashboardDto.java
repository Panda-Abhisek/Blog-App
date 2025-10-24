package com.panda.blogapp.dto;

import java.util.List;

import com.panda.blogapp.entity.Blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDto {
    private long blogs;
    private long comments;
    private long drafts;
    private List<Blog> recentBlogs;
}
