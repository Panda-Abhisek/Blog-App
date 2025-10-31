package com.panda.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.Comment;
import com.panda.blogapp.entity.User;

@DataJpaTest
@ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private Blog blog1;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1 = userRepository.save(user1);

        blog1 = new Blog();
        blog1.setUser(user1);
        blog1.setTitle("Sample Blog");
        blog1.setDescription("Blog Description");
        blog1.setPublished(true);
        blog1 = blogRepository.save(blog1);

        comment1 = new Comment();
        comment1.setName("Alice");
        comment1.setContent("Great post!");
        comment1.setBlog(blog1);
        commentRepository.save(comment1);

        comment2 = new Comment();
        comment2.setName("Bob");
        comment2.setContent("Thanks for sharing.");
        comment2.setBlog(blog1);
        commentRepository.save(comment2);
    }

    @Test
    void testFindCommentsByBlogId() {
        List<Comment> comments = commentRepository.findCommentsByBlogId(blog1.getId());
        assertThat(comments).isNotEmpty();
        assertThat(comments).extracting("blog.id").containsOnly(blog1.getId());
    }

    @Test
    void testFindByName() {
        List<Comment> comments = commentRepository.findByName("Alice");
        assertThat(comments).isNotEmpty();
        assertThat(comments.get(0).getName()).isEqualTo("Alice");
    }

    @Test
    void testCountByBlogUser() {
        long count = commentRepository.countByBlogUser(user1);
        assertThat(count).isEqualTo(2);
    }
}
