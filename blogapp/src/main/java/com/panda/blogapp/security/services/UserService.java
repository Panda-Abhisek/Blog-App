package com.panda.blogapp.security.services;

import java.util.Optional;

import com.panda.blogapp.entity.User;

import jakarta.validation.Valid;

public interface UserService {

	User registerUser(@Valid User user);

	Optional<User> findByEmail(String email);

	void generatePasswordResetToken(String email);

	void resetPassword(String token, String newPassword);

}
