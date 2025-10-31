package com.panda.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;

@DataJpaTest
@ActiveProfiles("test")
public class BlogRepositoryTest {

    @Autowired
    private BlogRepository blogRepository;
    
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");    // set email
        user1.setPassword("password1");
        userRepository.save(user1);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");    // set email
        user2.setPassword("password2");
        userRepository.save(user2);

        // Insert some blogs for testing
        Blog blog1 = new Blog();
        blog1.setUser(user1);
        blog1.setTitle("Spring Boot Tutorial");
        blog1.setDescription("Learn Spring Boot");
        blog1.setPublished(false);

        Blog blog2 = new Blog();
        blog2.setUser(user1);
        blog2.setTitle("Java Basics");
        blog2.setDescription("Java Programming Basics");
        blog2.setPublished(true);

        Blog blog3 = new Blog();
        blog3.setUser(user2);
        blog3.setTitle("Hibernate Tips");
        blog3.setDescription("Using Hibernate effectively");
        blog3.setPublished(true);

        blogRepository.save(blog1);
        blogRepository.save(blog2);
        blogRepository.save(blog3);
    }

    @Test
    void testCountByPublishedFalse() {
        long count = blogRepository.countByPublishedFalse();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testFindTop5ByUserOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Blog> blogs = blogRepository.findTop5ByUserOrderByCreatedAtDesc(user1, pageable);
        assertThat(blogs).isNotEmpty();
        assertThat(blogs.size()).isLessThanOrEqualTo(5);
        for (Blog blog : blogs) {
            assertThat(blog.getUser()).isEqualTo(user1);
        }
    }

    @Test
    void testFindByUser() {
        List<Blog> blogs = blogRepository.findByUser(user1);
        assertThat(blogs).isNotEmpty();
        for (Blog blog : blogs) {
            assertThat(blog.getUser()).isEqualTo(user1);
        }
    }

    @Test
    void testCountByUserAndPublishedTrue() {
        long count = blogRepository.countByUserAndPublishedTrue(user1);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testFindAllByPublishedTrue() {
        List<Blog> blogs = blogRepository.findAllByPublishedTrue();
        assertThat(blogs).isNotEmpty();
        for (Blog blog : blogs) {
            assertThat(blog.isPublished()).isTrue();
        }
    }

    @Test
    void testFindAllWithUser() {
        List<Blog> blogs = blogRepository.findAllWithUser();
        assertThat(blogs).isNotEmpty();
        for (Blog blog : blogs) {
            assertThat(blog.getUser()).isNotNull();
        }
    }

    @Test
    void testFindByUserWithUserFetched() {
        List<Blog> blogs = blogRepository.findByUserWithUserFetched(user1);
        assertThat(blogs).isNotEmpty();
        for (Blog blog : blogs) {
            assertThat(blog.getUser()).isEqualTo(user1);
        }
    }

    @Test
    void testFindByTitleContainingIgnoreCaseOrDescriptionContaining() {
        List<Blog> blogs = blogRepository.findByTitleContainingIgnoreCaseOrDescriptionContaining("spring", "spring");
        assertThat(blogs).isNotEmpty();
        for (Blog blog : blogs) {
            boolean titleCheck = blog.getTitle().toLowerCase().contains("spring");
            boolean descCheck = blog.getDescription().toLowerCase().contains("spring");
            assertThat(titleCheck || descCheck).isTrue();
        }
    }
}
