package com.panda.blogapp.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panda.blogapp.dto.LoginRequestDto;
import com.panda.blogapp.dto.LoginResponseDto;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.security.jwt.JwtUtil;
import com.panda.blogapp.security.services.UserDetailsImpl;
import com.panda.blogapp.security.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final UserService userService;
	private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
	
//	POST /api/auth/register
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
		User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userService.registerUser(newUser);
        
        if(saved != null)
        	return new ResponseEntity<>(saved, HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register user");
	}
	
//	POST /api/auth/login
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDto dto) {
		System.out.println("Entered login endpoint in auth controller - Username + Password - " + dto.getUsername()
				+ " - " + dto.getPassword());
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getUsername(),
				dto.getPassword());

		Authentication authentication = authenticationManager.authenticate(token);
		boolean authenticated = authentication.isAuthenticated();
		UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
		System.out.println(principal);
		if (authenticated) {
			String jwt = jwtUtil.generateTokenFromUsername(principal);
			User byUsername = userRepository.findByUsername(jwtUtil.getUserNameFromJwtToken(jwt))
					.orElseThrow(() -> new RuntimeException("User not found"));
//			System.out.println(jwt);
			LoginResponseDto response = new LoginResponseDto();
			response.setJwt(jwt);
			response.setUser(byUsername);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Failed to login user", HttpStatus.BAD_REQUEST);
		}
	}
}
