package com.panda.blogapp.security.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.panda.blogapp.entity.PasswordResetToken;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.PasswordResetTokenRepository;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.service.impl.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	
	@Value("${frontend.url}")
	private String frontendUrl;

	@Override
	public User registerUser(@Valid User user) {
		return userRepository.save(user);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void generatePasswordResetToken(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		String token = UUID.randomUUID().toString();
		Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
		PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
		passwordResetTokenRepository.save(resetToken);
		String resetUrl = frontendUrl + "/reset-password?token=" + token;
		emailService.sendPasswordResetMail(user.getEmail(), resetUrl);
	}

	@Override
	public void resetPassword(String token, String newPassword) {
		PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid Reset Token"));
		System.out.println(resetToken);
		if(resetToken.isUsed()) {
			throw new RuntimeException("Used Token");
		}
		if(resetToken.getExpiryDate().isBefore(Instant.now())) {
			throw new RuntimeException("Token Already Expired!");
		}
		User user = resetToken.getUser();
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		resetToken.setUsed(true);
		passwordResetTokenRepository.save(resetToken);
	}
	
}
