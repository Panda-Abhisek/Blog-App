package com.panda.blogapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.blogapp.dto.CommentDto;
import com.panda.blogapp.dto.CreateCommentRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.Comment;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.mapper.CommentMapper;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.CommentRepository;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.security.jwt.JwtUtil;
import com.panda.blogapp.service.CommentService;
import com.panda.blogapp.service.impl.EmailNotificationService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CommentController.class)
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BlogRepository blogRepository;

    @MockBean
    private CommentMapper mapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailNotificationService emailNotificationService;
    
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;


    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto commentDto;
    private CreateCommentRequest createCommentRequest;

    @BeforeEach
    void setup() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setName("John Doe");
        commentDto.setContent("This is a comment content");
        commentDto.setApproved(true);
        commentDto.setCreatedAt(LocalDateTime.now().minusDays(1));
        commentDto.setBlogTitle("Sample Blog Title");

        createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setName("John Doe");
        createCommentRequest.setContent("This is a comment content");
        createCommentRequest.setBlogId(1L);
    }

    @Test
    void testGetAllComments() throws Exception {
        List<CommentDto> comments = List.of(commentDto);
        when(commentService.getAllComments()).thenReturn(comments);

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$[0].name").value(commentDto.getName()));
    }

    @Test
    void testGetCommentsByBlog() throws Exception {
        List<CommentDto> comments = List.of(commentDto);
        when(commentService.getCommentsByBlog(1L)).thenReturn(comments);

        mockMvc.perform(get("/api/comments/blog")
                    .param("blogId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].blogTitle").value(commentDto.getBlogTitle()));
    }

    @Test
    void testAddComment() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("hashedpassword");

        Blog blog = new Blog();
        blog.setId(1L);
        blog.setTitle("Sample Blog");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName(createCommentRequest.getName());
        comment.setContent(createCommentRequest.getContent());
        comment.setBlog(blog);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Expected comment content");
        commentDto.setName("Author Name");
        commentDto.setApproved(false);
        commentDto.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        commentDto.setBlogTitle(blog.getTitle());

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(blogRepository.findById(createCommentRequest.getBlogId())).thenReturn(Optional.of(blog));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(mapper.toDto(comment)).thenReturn(commentDto);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", null));

        MvcResult result = mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response JSON: " + responseContent);
    }

    @Test
    void testApproveComment() throws Exception {
        doNothing().when(commentService).approveComment(1L);

        mockMvc.perform(put("/api/comments/1/approve"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk());
    }
}
