package com.panda.blogapp.service;

import java.util.List;

import com.panda.blogapp.dto.BlogDto;
import com.panda.blogapp.dto.CreateBlogRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;

import jakarta.validation.Valid;

public interface BlogService {

	List<BlogDto> getAllBlogs();

	BlogDto getBlogById(Long id);

	BlogDto createBlog(@Valid CreateBlogRequest request);

	List<BlogDto> getBlogsForCurrentUser();

	List<Blog> getBlogsByUser(User user);

}
