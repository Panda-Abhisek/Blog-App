package com.panda.blogapp.security.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public User registerUser(@Valid User user) {
		return userRepository.save(user);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
}
