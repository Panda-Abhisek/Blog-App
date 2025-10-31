package com.panda.blogapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.panda.blogapp.dto.BlogDto;
import com.panda.blogapp.dto.CreateBlogRequest;
import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.mapper.BlogMapper;
import com.panda.blogapp.repository.BlogRepository;
import com.panda.blogapp.repository.UserRepository;


public class BlogServiceImplTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlogMapper mapper;

    @InjectMocks
    private BlogServiceImpl blogService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User user;
    private Blog blog;
    private BlogDto blogDto;
    private CreateBlogRequest createBlogRequest;
    private List<Blog> blogEntities;
    private List<BlogDto> blogDtos;
    private List<Blog> publishedBlogs;
    private List<BlogDto> publishedBlogDtos;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("user1");

        blog = new Blog();
        blog.setId(1L);
        blog.setUser(user);
        blog.setPublished(false);

        blogDto = new BlogDto();
		blogDto.setPublished(true);

		blogEntities = List.of(new Blog(1L, "Title 1", "Content 1", null, null, null, false, null, null, null, new User()),
				new Blog(2L, "Title 2", "Content 2", null, null, null, false, null, null, null, new User()));
		
		blogDtos = List.of(
			    new BlogDto() {{
			        setId(1L);
			        setTitle("Title 1");
			        setSubTitle("SubTitle 1");
			        setDescription("Description 1");
			        setCategory("Category 1");
			        setImage("Image1.jpg");
			        setPublished(true);
			        setCreatedAt(LocalDateTime.now().minusDays(1));
			        setUpdatedAt(LocalDateTime.now());
			        setUsername("user1");
			    }},
			    new BlogDto() {{
			        setId(2L);
			        setTitle("Title 2");
			        setSubTitle("SubTitle 2");
			        setDescription("Description 2");
			        setCategory("Category 2");
			        setImage("Image2.jpg");
			        setPublished(false);
			        setCreatedAt(LocalDateTime.now().minusDays(2));
			        setUpdatedAt(LocalDateTime.now().minusHours(5));
			        setUsername("user2");
			    }}
			);

		publishedBlogs = List.of(
	            new Blog(1L, "Title 1","SubTitle 1", "Description 1","Category 1","Image1.jpg",true,LocalDateTime.now().minusDays(2), LocalDateTime.now(), null, user),
	            new Blog(2L, "Title 2","SubTitle 1", "Description 1","Category 1","Image1.jpg",true,LocalDateTime.now().minusDays(2), LocalDateTime.now(), null, user)
	        );

	        publishedBlogDtos = List.of(
	            new BlogDto() {{
	                setId(1L);
	                setTitle("Title 1");
	                setPublished(true);
	            }},
	            new BlogDto() {{
	                setId(2L);
	                setTitle("Title 2");
	                setPublished(true);
	            }}
	        );

        createBlogRequest = new CreateBlogRequest();
        // Set necessary fields for createBlogRequest here

        // Mock Spring Security context to return the username "user1"
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user1");
        SecurityContextHolder.setContext(securityContext);
    }
    
    @Test
    void testGetAllBlogsReturnsDtoList() {
        when(blogRepository.findAllWithUser()).thenReturn(blogEntities);
        when(mapper.toDtoList(blogEntities)).thenReturn(blogDtos);

        List<BlogDto> result = blogService.getAllBlogs();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(blogRepository).findAllWithUser();
        verify(mapper).toDtoList(blogEntities);
    }

    @Test
    void testGetBlogByIdReturnsDto() {
        Blog blog = blogEntities.get(0);
        BlogDto dto = blogDtos.get(0);

        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(mapper.toDto(blog)).thenReturn(dto);

        BlogDto result = blogService.getBlogById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(blogRepository).findById(1L);
        verify(mapper).toDto(blog);
    }

    @Test
    void testGetBlogByIdThrowsExceptionWhenNotFound() {
        when(blogRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            blogService.getBlogById(1L);
        });

        assertEquals("Blog not found.", ex.getMessage());
        verify(blogRepository).findById(1L);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetBlogsForCurrentUser() {
        // Set up the Security Context to return "testuser"
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken("testuser", null);
        SecurityContextHolder.setContext(
            SecurityContextHolder.createEmptyContext()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Mock repository calls
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(blogRepository.findByUser(user)).thenReturn(blogEntities);
        when(mapper.toDtoList(blogEntities)).thenReturn(blogDtos);

        // Call the service method
        List<BlogDto> result = blogService.getBlogsForCurrentUser();

        // Validate results
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByUsername("testuser");
        verify(blogRepository).findByUser(user);
        verify(mapper).toDtoList(blogEntities);
    }

    @Test
    void testGetBlogsForCurrentUser_UserNotFound() {
        // Set the security context to username "unknown"
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken("unknown", null));

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            blogService.getBlogsForCurrentUser();
        });
    }

    @Test
    void testGetBlogsByUser() {
        when(blogRepository.findByUserWithUserFetched(user)).thenReturn(blogEntities);

        List<Blog> result = blogService.getBlogsByUser(user);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(blogRepository).findByUserWithUserFetched(user);
    }
    
    @Test
    void testTogglePublishSuccess() {
        Long id = 1L;
        Blog blog = new Blog();
        blog.setId(id);
        blog.setPublished(false);

        BlogDto inputDto = new BlogDto();
        inputDto.setPublished(true);

        BlogDto outputDto = new BlogDto();
        outputDto.setId(id);
        outputDto.setPublished(true);

        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));
        when(blogRepository.save(blog)).thenReturn(blog);
        when(mapper.toDto(blog)).thenReturn(outputDto);

        BlogDto result = blogService.togglePublish(id, inputDto);

        assertNotNull(result);
        assertEquals(true, result.isPublished());
        verify(blogRepository).findById(id);
        verify(blogRepository).save(blog);
        verify(mapper).toDto(blog);
    }

    @Test
    void testTogglePublishBlogNotFound() {
        Long id = 1L;
        BlogDto inputDto = new BlogDto();
        inputDto.setPublished(true);

        when(blogRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            blogService.togglePublish(id, inputDto);
        });

        assertEquals("Blog not found", ex.getMessage());
        verify(blogRepository).findById(id);
        verify(blogRepository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }
    
    @Test
    void testGetAllPublishedBlogs() {
        when(blogRepository.findAllByPublishedTrue()).thenReturn(publishedBlogs);
        when(mapper.toDtoList(publishedBlogs)).thenReturn(publishedBlogDtos);

        List<BlogDto> result = blogService.getAllPublishedBlogs();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(BlogDto::isPublished));

        verify(blogRepository).findAllByPublishedTrue();
        verify(mapper).toDtoList(publishedBlogs);
    }

    @Test
    void testCreateBlog() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(mapper.toEntity(createBlogRequest)).thenReturn(blog);
        when(blogRepository.save(blog)).thenReturn(blog);
        when(mapper.toDto(blog)).thenReturn(blogDto);

        BlogDto result = blogService.createBlog(createBlogRequest);

        assertThat(result).isEqualTo(blogDto);
        verify(blogRepository).save(blog);
    }

    @Test
    void testCreateBlogUserNotFound() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            blogService.createBlog(createBlogRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    void testDeleteBlogAccessDenied() {
        Blog blogOwnedByOtherUser = new Blog();
        User anotherUser = new User();
        anotherUser.setUsername("otherUser");
        blogOwnedByOtherUser.setUser(anotherUser);

        when(blogRepository.findById(1L)).thenReturn(Optional.of(blogOwnedByOtherUser));
        when(authentication.getName()).thenReturn("user1");

        assertThrows(AccessDeniedException.class, () -> blogService.deleteBlog(1L));
    }

    @Test
    void testDeleteBlogSuccess() {
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(authentication.getName()).thenReturn("user1");

        blogService.deleteBlog(1L);

        verify(blogRepository).delete(blog);
    }
    
    @Test
    void testSearchBlogs() {
        String query = "title";
        when(blogRepository.findByTitleContainingIgnoreCaseOrDescriptionContaining(query, query)).thenReturn(blogEntities);
        when(mapper.toDtoList(blogEntities)).thenReturn(blogDtos);

        List<BlogDto> result = blogService.searchBlogs(query);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getTitle().toLowerCase().contains("title")));

        verify(blogRepository).findByTitleContainingIgnoreCaseOrDescriptionContaining(query, query);
        verify(mapper).toDtoList(blogEntities);
    }

    // Additional tests for other methods can follow the same pattern...
}
