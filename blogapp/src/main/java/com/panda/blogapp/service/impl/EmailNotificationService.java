package com.panda.blogapp.service.impl;

import org.springframework.stereotype.Service;

import com.panda.blogapp.dto.CreateCommentRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {
	
	private final BlogRepository blogRepository;
	private final UserRepository userRepository;
	private final EmailService emailService;

	public void notifyBlogAuthor(CreateCommentRequest request) {
		   Blog blog = blogRepository.findById(request.getBlogId()).orElseThrow(() -> new RuntimeException("Blog not found"));
		   User author = userRepository.findById(blog.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));
		   String emailBody = String.format(
		       "%s has commented on your blog - \"%s\":\n%s",
		       request.getName(), blog.getTitle(), request.getContent()
		   );
		   emailService.sendEmail(author.getEmail(), "New Comment Notification", emailBody);
		}


}
