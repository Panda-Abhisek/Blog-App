package com.panda.blogapp.service;

import java.util.List;

import com.panda.blogapp.dto.CommentDto;
import com.panda.blogapp.dto.CreateCommentRequest;

import jakarta.validation.Valid;

public interface CommentService {

	List<CommentDto> getAllComments();

	CommentDto getCommentById(Long id);

	List<CommentDto> getCommentsByBlog(Long blogId);

	CommentDto addComment(@Valid CreateCommentRequest request);

	void approveComment(Long id);

	void deleteComment(Long id);

}
