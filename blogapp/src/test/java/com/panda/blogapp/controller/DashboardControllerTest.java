package com.panda.blogapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.blogapp.dto.DashboardDto;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.CommentRepository;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.security.jwt.JwtUtil;
import com.panda.blogapp.service.BlogService;

@WebMvcTest(controllers = DashboardController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogRepository blogRepo;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private CommentRepository commentRepo;

    @MockBean
    private BlogService blogService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private List<Blog> recentBlogs;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("hashedpassword");

        Blog blog1 = new Blog();
        blog1.setId(1L);
        blog1.setTitle("Blog 1");
        blog1.setUser(testUser);
        blog1.setPublished(true);

        Blog blog2 = new Blog();
        blog2.setId(2L);
        blog2.setTitle("Blog 2");
        blog2.setUser(testUser);
        blog2.setPublished(true);

        recentBlogs = List.of(blog1, blog2);
    }

    @Test
    void testGetDashboard() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(blogRepo.countByUserAndPublishedTrue(testUser)).thenReturn(2L);
        when(commentRepo.countByBlogUser(testUser)).thenReturn(5L);
        when(blogRepo.countByPublishedFalse()).thenReturn(1L);
        when(blogRepo.findTop5ByUserOrderByCreatedAtDesc(eq(testUser), any(Pageable.class))).thenReturn(recentBlogs);

        // Mock authentication for security context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password"));

        MvcResult result = mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        DashboardDto dto = objectMapper.readValue(json, DashboardDto.class);

        assertEquals(2L, dto.getBlogs());
        assertEquals(5L, dto.getComments());
        assertEquals(1L, dto.getDrafts());
        assertEquals(2, dto.getRecentBlogs().size());
    }

    @Test
    void testListBlogs() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(blogService.getBlogsByUser(testUser)).thenReturn(recentBlogs);

        MvcResult result = mockMvc.perform(get("/api/dashboard/listblog")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Blog[] blogs = objectMapper.readValue(json, Blog[].class);

        assertEquals(2, blogs.length);
        assertEquals("Blog 1", blogs[0].getTitle());
        assertEquals("Blog 2", blogs[1].getTitle());
    }
}
