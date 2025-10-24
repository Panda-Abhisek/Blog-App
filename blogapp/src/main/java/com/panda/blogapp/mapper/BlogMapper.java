package com.panda.blogapp.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.panda.blogapp.dto.BlogDto;
import com.panda.blogapp.dto.CreateBlogRequest;
import com.panda.blogapp.entity.Blog;

@Mapper(componentModel = "spring")
public interface BlogMapper {

	@Mapping(source = "user.username", target = "username")
    BlogDto toDto(Blog blog);
    List<BlogDto> toDtoList(List<Blog> blogs);

    Blog toEntity(CreateBlogRequest request);
}
