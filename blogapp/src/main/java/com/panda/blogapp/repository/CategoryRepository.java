package com.panda.blogapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.panda.blogapp.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
