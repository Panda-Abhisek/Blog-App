package com.panda.blogapp.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.panda.blogapp.dto.LoginRequestDto;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.security.jwt.JwtUtil;
import com.panda.blogapp.security.services.UserDetailsImpl;
import com.panda.blogapp.security.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
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
	    log.debug("Entered login endpoint in auth controller - Username + Password - " 
	        + dto.getUsername() + " - " + dto.getPassword());

	    UsernamePasswordAuthenticationToken authToken =
	            new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

	    Authentication authentication = authenticationManager.authenticate(authToken);
	    if (authentication.isAuthenticated()) {
	        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

	        // Generate JWT
	        String jwt = jwtUtil.generateTokenFromUsername(principal);
//	        System.out.println("Generated Jwt: " + jwt);

	        // Create HttpOnly cookie
	        ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
	                .httpOnly(true)
	                .secure(true) // use true in production (HTTPS)
	                .sameSite("Lax")
	                .path("/")
	                .maxAge(24 * 60 * 60) // 1 day
	                .build();

//	        System.out.println("jwtCookie: " + jwtCookie);
	        // Fetch user details (minus password)
	        User user = userRepository.findByUsername(principal.getUsername())
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        user.setPassword(null); // never send this back
//	        System.out.println("User: " + user);

	        return ResponseEntity.ok()
	                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	                .body(user); // no need to send token in body
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
	    }
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
	    String username = jwtUtil.getUsernameFromRequest(request);
//	    System.out.println("Username from request : " + username);
	    if (username == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    User user = userRepository.findByUsername(username)
	        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	    user.setPassword(null);
	    return ResponseEntity.ok(user);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
	    ResponseCookie cookie = ResponseCookie.from("jwt", "")
	            .httpOnly(true)
	            .path("/")
	            .maxAge(0)
	            .build();

	    return ResponseEntity.ok()
	            .header(HttpHeaders.SET_COOKIE, cookie.toString())
	            .body("Logged out successfully");
	}

	@GetMapping("/csrf-token")
	public CsrfToken getCsrfToken(CsrfToken token) {
	    return token; // Spring will automatically add XSRF-TOKEN cookie
	}
	
	@PostMapping("/public/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestParam String email) {
		try {
			System.out.println("Entered forgotpassword");
			userService.generatePasswordResetToken(email);
			return ResponseEntity.ok("Password Reset Email Sent");
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Sending Password Reset Email");
		}
	}
	
	@PostMapping("/public/reset-password")
	public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
		try {
			System.out.println("Entered resetPassword");
			userService.resetPassword(token, newPassword);
			System.out.println("Exited resetPassword");
			return ResponseEntity.ok("Password Reset Successful");
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
}
