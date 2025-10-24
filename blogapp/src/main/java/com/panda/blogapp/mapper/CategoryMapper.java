package com.panda.blogapp.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.panda.blogapp.dto.CategoryDto;
import com.panda.blogapp.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);
    List<CategoryDto> toDtoList(List<Category> categories);

    // Usually, categories are created/updated differently,
    // so this method depends on your use case.
    // Define if you have a CategoryCreateRequest DTO.
}
