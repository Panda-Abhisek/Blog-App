package com.panda.blogapp.dto;

import com.panda.blogapp.entity.User;

import lombok.Data;

@Data
public class LoginResponseDto {
	private String jwt;
	private User user;
}
