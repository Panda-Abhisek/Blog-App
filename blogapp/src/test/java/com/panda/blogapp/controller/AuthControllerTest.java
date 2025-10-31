package com.panda.blogapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.blogapp.dto.LoginRequestDto;
import com.panda.blogapp.entity.User;
import com.panda.blogapp.repository.UserRepository;
import com.panda.blogapp.security.jwt.JwtUtil;
import com.panda.blogapp.security.services.UserDetailsImpl;
import com.panda.blogapp.security.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@MockBean
	private UserRepository userRepository;

	// Test for /register endpoint
	@Test
	void registerUser_success() throws Exception {
		User user = new User();
		user.setUsername("testuser");
		user.setEmail("test@domain.com");
		user.setPassword("password");

		User savedUser = new User();
		savedUser.setId(1L);
		savedUser.setUsername("testuser");
		savedUser.setEmail("test@domain.com");
		savedUser.setPassword(null);

		when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
		when(userService.registerUser(any(User.class))).thenReturn(savedUser);

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(user))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"));
	}

	// Test for /login endpoint
	@Test
	void loginUser_success() throws Exception {
		LoginRequestDto loginDto = new LoginRequestDto();
		loginDto.setUsername("testuser");
		loginDto.setPassword("password");

		UserDetailsImpl principal = mock(UserDetailsImpl.class);
		when(principal.getUsername()).thenReturn("testuser");
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(principal);
		when(authentication.isAuthenticated()).thenReturn(true);
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
		when(jwtUtil.generateTokenFromUsername(any(UserDetailsImpl.class))).thenReturn("fake-jwt-token");

		User user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setEmail("test@domain.com");
		user.setPassword(null);

		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginDto))).andExpect(status().isOk())
				.andExpect(cookie().exists("jwt")).andExpect(jsonPath("$.username").value("testuser"));
	}

	// More tests for /me, /logout, /csrf-token endpoints can be added similarly
	@Test
	void getCurrentUser_authenticated_returnsUser() throws Exception {
		String username = "testuser";

		User user = new User();
		user.setId(1L);
		user.setUsername(username);
		user.setEmail("test@example.com");
		user.setPassword(null); // password should be null in response

		// Mock jwtUtil to return username extracted from request
		when(jwtUtil.getUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(username);
		// Mock userRepository to return the user for username
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

		mockMvc.perform(get("/api/auth/me")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(username))
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void getCurrentUser_unauthenticated_returnsUnauthorized() throws Exception {
		// Mock jwtUtil to return null username (unauthenticated)
		when(jwtUtil.getUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(null);

		mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
	}

	@Test
	void logout_clearsJwtCookie() throws Exception {
		mockMvc.perform(post("/api/auth/logout")).andDo(print()).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt=;")))
				.andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")))
				.andExpect((ResultMatcher) content().string("Logged out successfully"));
	}

//	@Test
//	@WithMockUser
//	void getCsrfToken_ReturnsToken() throws Exception {
//		MockHttpSession session = new MockHttpSession();
//		mockMvc.perform(get("/api/auth/csrf-token").session(session)).andDo(print()).andExpect(status().isOk())
//				.andExpect(cookie().exists("XSRF-TOKEN")).andExpect(jsonPath("$.token").exists());
//	}

}
