package com.panda.blogapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.panda.blogapp.dto.CommentDto;
import com.panda.blogapp.dto.CreateCommentRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.Comment;
import com.panda.blogapp.mapper.CommentMapper;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.CommentRepository;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	private final CommentRepository commentRepository;
	private final BlogRepository blogRepository;
	private final CommentMapper mapper;
	private final UserRepository userRepository;

	// Get all comments (could be admin or filtered by blog id in a real app)
	@GetMapping
	public List<CommentDto> getAllComments() {
		return commentService.getAllComments();
	}

	// Get comments for a particular blog post
	@GetMapping("/blog")
	public List<CommentDto> getCommentsByBlog(@RequestParam Long blogId) {
		return commentService.getCommentsByBlog(blogId);
	}

	// Post a new comment under a blog
	@PostMapping
	public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CreateCommentRequest request) {
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		userRepository.findByUsername(user).orElseThrow(() -> new RuntimeException("User not found"));
		
		Blog blog = blogRepository.findById(request.getBlogId())
				.orElseThrow(() -> new RuntimeException("Blog not found"));

		Comment comment = new Comment();
		comment.setName(request.getName());
		comment.setContent(request.getContent());
		comment.setBlog(blog); // this is key
		commentRepository.save(comment);
		
		CommentDto newComment = mapper.toDto(comment);
		return new ResponseEntity<>(newComment, HttpStatus.CREATED);
	}

	// Optional: Approve or delete comment endpoints can be added later for
	@PutMapping("/{id}/approve")
	public ResponseEntity<?> approveComment(@PathVariable Long id) {
	    commentService.approveComment(id);
	    return ResponseEntity.ok("Comment approved successfully");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable Long id) {
		commentService.deleteComment(id);
	    return ResponseEntity.ok("Comment deleted successfully");
	}

}
