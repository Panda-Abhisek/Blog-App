package com.panda.blogapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.blogapp.dto.BlogDto;
import com.panda.blogapp.dto.CreateBlogRequest;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.security.jwt.JwtUtil;
import com.panda.blogapp.service.BlogService;

@ActiveProfiles("test")
@WebMvcTest(controllers = BlogController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private BlogService blogService;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private BlogDto testBlogDto;
    private CreateBlogRequest createBlogRequest;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("hashedpassword");

        createBlogRequest = new CreateBlogRequest();
        createBlogRequest.setTitle("New Blog Title");
        createBlogRequest.setSubTitle("New Blog Subtitle");
        createBlogRequest.setDescription("Blog description content");
        createBlogRequest.setCategory("Technology");
        createBlogRequest.setImage("image-url.jpg");
        createBlogRequest.setPublished(true);

        testBlogDto = new BlogDto();
        testBlogDto.setId(1L);
        testBlogDto.setTitle("Test Blog Title");
        testBlogDto.setSubTitle("Test Blog Subtitle");
        testBlogDto.setDescription("Test blog description");
        testBlogDto.setCategory("Technology");
        testBlogDto.setImage("test-image.jpg");
        testBlogDto.setPublished(true);
        testBlogDto.setCreatedAt(LocalDateTime.now().minusDays(1));
        testBlogDto.setUpdatedAt(LocalDateTime.now());
        testBlogDto.setUsername(testUser.getUsername());
    }


    @Test
    void testGetAllBlogs() throws Exception {
        List<BlogDto> blogs = List.of(testBlogDto);
        when(blogService.getAllBlogs()).thenReturn(blogs);

        mockMvc.perform(get("/api/blogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBlogDto.getId()))
                .andExpect(jsonPath("$[0].title").value(testBlogDto.getTitle()));
    }

    @Test
    void testGetAllPublishedBlogs() throws Exception {
        List<BlogDto> blogs = List.of(testBlogDto);
        when(blogService.getAllPublishedBlogs()).thenReturn(blogs);

        mockMvc.perform(get("/api/blogs/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBlogDto.getId()));
    }

    @Test
    void testGetBlogById() throws Exception {
        when(blogService.getBlogById(1L)).thenReturn(testBlogDto);

        mockMvc.perform(get("/api/blogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBlogDto.getId()))
                .andExpect(jsonPath("$.title").value(testBlogDto.getTitle()));
    }

    @Test
    void testCreateBlog() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(blogService.createBlog(any(CreateBlogRequest.class))).thenReturn(testBlogDto);

        // Mock SecurityContext for authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", null));

        mockMvc.perform(post("/api/blogs")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createBlogRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testBlogDto.getId()))
                .andExpect(jsonPath("$.title").value(testBlogDto.getTitle()));
    }

    @Test
    void testGetUserBlogs() throws Exception {
        List<BlogDto> blogs = List.of(testBlogDto);
        when(blogService.getBlogsForCurrentUser()).thenReturn(blogs);

        mockMvc.perform(get("/api/blogs/user/blogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBlogDto.getId()));
    }

    @Test
    void testTogglePublish() throws Exception {
        // Mock the service method to return a sample BlogDto
        when(blogService.togglePublish(eq(1L), any(BlogDto.class))).thenReturn(testBlogDto);

        mockMvc.perform(put("/api/blogs/1/publish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testBlogDto)))
                .andExpect(status().isOk());
    }


    @Test
    void testDeleteBlog() throws Exception {
        doNothing().when(blogService).deleteBlog(1L);

        mockMvc.perform(delete("/api/blogs/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchBlogs() throws Exception {
        List<BlogDto> blogs = List.of(testBlogDto);
        when(blogService.searchBlogs("test")).thenReturn(blogs);

        mockMvc.perform(get("/api/blogs/search")
                    .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBlogDto.getId()));
    }
}
