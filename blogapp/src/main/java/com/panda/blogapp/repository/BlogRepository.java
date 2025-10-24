package com.panda.blogapp.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.panda.blogapp.entity.Blog;
import com.panda.blogapp.entity.User;

public interface BlogRepository extends JpaRepository<Blog, Long>{
	public long countByPublishedFalse();
	
	@Query("SELECT b FROM Blog b JOIN FETCH b.user WHERE b.user = :user ORDER BY b.createdAt DESC")
	List<Blog> findTop5ByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

	List<Blog> findByUser(User user);
	
	@Query("SELECT b FROM Blog b JOIN FETCH b.user")
	List<Blog> findAllWithUser();

	@Query("SELECT b FROM Blog b JOIN FETCH b.user WHERE b.user = :user")
	List<Blog> findByUserWithUserFetched(@Param("user") User user);

}
