package com.panda.blogapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.panda.blogapp.dto.BlogDto;
import com.panda.blogapp.dto.CreateBlogRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final UserRepository userRepository;

    @GetMapping
    public List<BlogDto> getAllBlogs() {
        return blogService.getAllBlogs();
    }
    
    @GetMapping("/all")
    public List<BlogDto> getAllPublishedBlogs() {
    	return blogService.getAllPublishedBlogs(); 
    }

    @GetMapping("/{id}")
    public BlogDto getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id);
    }

    @PostMapping
    public ResponseEntity<BlogDto> createBlog(@Valid @RequestBody CreateBlogRequest request) {
    	String name = SecurityContextHolder.getContext().getAuthentication().getName();
    	User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	
        BlogDto blogDto = blogService.createBlog(request);
        return new ResponseEntity<>(blogDto, HttpStatus.CREATED);
    }

    @GetMapping("/user/blogs")
    public List<BlogDto> getUserBlogs() {
        return blogService.getBlogsForCurrentUser();
    }
    // Add update, delete endpoints as needed
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> togglePublish(@PathVariable Long id, @RequestBody BlogDto blogDto) {
    	try {
    		blogService.togglePublish(id, blogDto);
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
        return ResponseEntity.ok("Updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok("Blog deleted successfully");
    }
    
    @GetMapping("/search")
    public List<BlogDto> searchBlogs(@RequestParam String query) {
    	return blogService.searchBlogs(query);
    }


}
