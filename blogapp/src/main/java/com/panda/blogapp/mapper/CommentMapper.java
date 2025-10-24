package com.panda.blogapp.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.panda.blogapp.dto.CommentDto;
import com.panda.blogapp.dto.CreateCommentRequest;
import com.panda.blogapp.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	@Mapping(source = "blog.title", target = "blogTitle")
    CommentDto toDto(Comment comment);
    List<CommentDto> toDtoList(List<Comment> comments);

    Comment toEntity(CreateCommentRequest request);
}
