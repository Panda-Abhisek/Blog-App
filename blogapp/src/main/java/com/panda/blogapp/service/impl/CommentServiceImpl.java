package com.panda.blogapp.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panda.blogapp.dto.CommentDto;
import com.panda.blogapp.dto.CreateCommentRequest;
import com.panda.blogapp.entity.Comment;
import com.panda.blogapp.mapper.CommentMapper;
import com.panda.blogapp.repository.CommentRepository;
import com.panda.blogapp.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final CommentMapper mapper;

	@Override
	public List<CommentDto> getAllComments() {
		return mapper.toDtoList(commentRepository.findAll());
	}

	@Override
	public CommentDto getCommentById(Long id) {
		return mapper.toDto(commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found.")));
	}

	@Override
	public CommentDto addComment(@Valid CreateCommentRequest request) {
		return mapper.toDto(commentRepository.save(mapper.toEntity(request)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommentDto> getCommentsByBlog(Long blogId) {
		return mapper.toDtoList(commentRepository.findCommentsByBlogId(blogId));
	}

	@Override
	public void approveComment(Long id) {
		Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
		comment.setApproved(true);
		commentRepository.save(comment);
	}

	@Override
	public void deleteComment(Long id) {
		commentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Comment not found"));
		commentRepository.deleteById(id);
	}

}
