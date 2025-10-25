package com.panda.blogapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panda.blogapp.entity.Comment;
import com.panda.blogapp.entity.User;

public interface CommentRepository extends JpaRepository<Comment, Long>{

	@Query("SELECT c FROM Comment c JOIN FETCH c.blog WHERE c.blog.id = :blogId")
	public List<Comment> findCommentsByBlogId(Long blogId);
	
	public List<Comment> findByName(String name);

	public long countByBlogUser(User user);

}
