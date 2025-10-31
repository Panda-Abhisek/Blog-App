package com.panda.blogapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import com.panda.blogapp.dto.CommentDto;
import com.panda.blogapp.dto.CreateCommentRequest;
import com.panda.blogapp.entity.Comment;
import com.panda.blogapp.mapper.CommentMapper;
import com.panda.blogapp.repository.CommentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper mapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private CommentDto commentDto;
    private CreateCommentRequest createCommentRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        comment = new Comment();
        comment.setId(1L);
        comment.setApproved(false);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setApproved(false);

        createCommentRequest = new CreateCommentRequest();
        // Initialize fields on createCommentRequest as needed
    }

    @Test
    void testGetAllComments() {
        when(commentRepository.findAll()).thenReturn(Arrays.asList(comment));
        when(mapper.toDtoList(anyList())).thenReturn(Arrays.asList(commentDto));

        List<CommentDto> dtos = commentService.getAllComments();

        assertThat(dtos).isNotEmpty();
        verify(commentRepository).findAll();
        verify(mapper).toDtoList(anyList());
    }

    @Test
    void testGetCommentByIdFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(mapper.toDto(comment)).thenReturn(commentDto);

        CommentDto dto = commentService.getCommentById(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
    }

    @Test
    void testGetCommentByIdNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.getCommentById(1L);
        });

        assertThat(exception.getMessage()).isEqualTo("Blog not found.");
    }

    @Test
    void testAddComment() {
        when(mapper.toEntity(createCommentRequest)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(mapper.toDto(comment)).thenReturn(commentDto);

        CommentDto dto = commentService.addComment(createCommentRequest);

        assertThat(dto).isEqualTo(commentDto);
        verify(commentRepository).save(comment);
    }

    @Test
    void testGetCommentsByBlog() {
        when(commentRepository.findCommentsByBlogId(1L)).thenReturn(Arrays.asList(comment));
        when(mapper.toDtoList(anyList())).thenReturn(Arrays.asList(commentDto));

        List<CommentDto> dtos = commentService.getCommentsByBlog(1L);

        assertThat(dtos).isNotEmpty();
    }

    @Test
    void testApproveComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        commentService.approveComment(1L);

        assertThat(comment.isApproved()).isTrue();
        verify(commentRepository).save(comment);
    }

    @Test
    void testApproveCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.approveComment(1L);
        });

        assertThat(exception.getMessage()).isEqualTo("Comment not found");
    }

    @Test
    void testDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void testDeleteCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(1L);
        });

        assertThat(exception.getMessage()).isEqualTo("Comment not found");
        verify(commentRepository, never()).deleteById(any());
    }
}
