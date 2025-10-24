package com.panda.blogapp.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.panda.blogapp.dto.CategoryDto;
import com.panda.blogapp.mapper.CategoryMapper;
import com.panda.blogapp.repository.CategoryRepository;
import com.panda.blogapp.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

	private final CategoryRepository categoryRepository;
	private final CategoryMapper mapper;
	
	@Override
	public List<CategoryDto> getAllCategories() {
		return mapper.toDtoList(categoryRepository.findAll());
	}

}
