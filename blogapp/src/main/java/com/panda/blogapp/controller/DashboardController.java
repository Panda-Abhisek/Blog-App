package com.panda.blogapp.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.panda.blogapp.dto.DashboardDto;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.CommentRepository;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.service.BlogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final BlogRepository blogRepo;
    private final CommentRepository commentRepo;
    private final BlogService blogService;
    private final UserRepository userRepository;


    @GetMapping
    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        long blogs = blogRepo.countByUserAndPublishedTrue(user);
        long comments = commentRepo.countByBlogUser(user);
        long drafts = blogRepo.countByPublishedFalse();
        Pageable pageable = PageRequest.of(0, 5);
        List<Blog> recent = blogRepo.findTop5ByUserOrderByCreatedAtDesc(user,pageable);

        return new DashboardDto(blogs, comments, drafts, recent);
    }
    
    @GetMapping("/listblog")
    public List<Blog> listBlogs(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return blogService.getBlogsByUser(user);
    }
}
