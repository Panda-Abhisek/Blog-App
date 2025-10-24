package com.panda.blogapp.security.services;

import com.panda.blogapp.entity.User;

import jakarta.validation.Valid;

public interface UserService {

	User registerUser(@Valid User user);

}
