package com.panda.blogapp.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.panda.blogapp.dto.BlogDto;
import com.panda.blogapp.dto.CreateBlogRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.mapper.BlogMapper;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

	private final BlogRepository blogRepository;
	private final BlogMapper mapper;
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public List<BlogDto> getAllBlogs() {
		return mapper.toDtoList(blogRepository.findAllWithUser());
	}

	@Override
	public BlogDto getBlogById(Long id) {
		return mapper.toDto(blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found.")));
	}

	@Override
	public BlogDto createBlog(@Valid @RequestBody CreateBlogRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		Blog entity = mapper.toEntity(request);
		entity.setUser(user);

		return mapper.toDto(blogRepository.save(entity));
	}

	@Override
	public List<BlogDto> getBlogsForCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<Blog> blogs = blogRepository.findByUser(user);
		return mapper.toDtoList(blogs);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Blog> getBlogsByUser(User user) {
		return blogRepository.findByUserWithUserFetched(user);
	}

	@Override
	@Transactional
	public BlogDto togglePublish(Long id, BlogDto blogDto) {
		Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
		blog.setPublished(blogDto.isPublished());
		blogRepository.save(blog);
		return mapper.toDto(blog);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BlogDto> getAllPublishedBlogs() {
		return mapper.toDtoList(blogRepository.findAllByPublishedTrue());
	}

	@Override
	public void deleteBlog(Long id) {
		Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!blog.getUser().getUsername().equals(username)) {
		    throw new AccessDeniedException("You can only delete your own blogs");
		}
		blogRepository.delete(blog);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BlogDto> searchBlogs(String query) {
		List<Blog> byTitleContainingIgnoreCaseOrContentContainingIgnoreCase = blogRepository.findByTitleContainingIgnoreCaseOrDescriptionContaining(query, query);
		return mapper.toDtoList(byTitleContainingIgnoreCaseOrContentContainingIgnoreCase);
	}

}
