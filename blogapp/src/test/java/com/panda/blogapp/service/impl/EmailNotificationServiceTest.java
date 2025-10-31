package com.panda.blogapp.service.impl;

import static org.mockito.Mockito.*;

import java.util.Optional;

import com.panda.blogapp.dto.CreateCommentRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EmailNotificationServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailNotificationService notificationService;

    private CreateCommentRequest createCommentRequest;

    private Blog blog;
    private User author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setBlogId(1L);
        createCommentRequest.setName("Commenter");
        createCommentRequest.setContent("Nice blog!");

        author = new User();
        author.setId(10L);
        author.setEmail("author@example.com");

        blog = new Blog();
        blog.setId(1L);
        blog.setTitle("Test Blog");
        blog.setUser(author);
    }

    @Test
    void testNotifyBlogAuthor() {
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(userRepository.findById(10L)).thenReturn(Optional.of(author));

        notificationService.notifyBlogAuthor(createCommentRequest);

        String expectedEmailBody = String.format(
            "%s has commented on your blog - \"%s\":\n%s",
            createCommentRequest.getName(),
            blog.getTitle(),
            createCommentRequest.getContent()
        );

        verify(emailService).sendEmail(author.getEmail(), "New Comment Notification", expectedEmailBody);
    }

    @Test
    void testNotifyBlogAuthorBlogNotFound() {
        when(blogRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            notificationService.notifyBlogAuthor(createCommentRequest);
        } catch (RuntimeException e) {
            assert(e.getMessage().equals("Blog not found"));
        }

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testNotifyBlogAuthorUserNotFound() {
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        try {
            notificationService.notifyBlogAuthor(createCommentRequest);
        } catch (RuntimeException e) {
            assert(e.getMessage().equals("User not found"));
        }

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
