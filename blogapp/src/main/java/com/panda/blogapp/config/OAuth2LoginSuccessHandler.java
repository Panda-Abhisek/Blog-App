package com.panda.blogapp.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.panda.blogapp.entity.User;
import com.panda.blogapp.security.jwt.JwtUtil;
import com.panda.blogapp.security.services.UserDetailsImpl;
import com.panda.blogapp.security.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtil jwtUtil;

    @Value("${frontend.url}")
    private String frontendUrl;

    String username;
    String idAttributeKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()) || "google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();
            if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = attributes.getOrDefault("login", "").toString();
                idAttributeKey = "id";
            } else if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = email.split("@")[0];
                idAttributeKey = "sub";
            } else {
                username = "";
                idAttributeKey = "id";
            }
            
            System.out.println("HELLO OAUTH: " + email + " : " + name + " : " + username);

            userService.findByEmail(email)
                    .ifPresentOrElse(user -> {
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    }, () -> {
                        User newUser = new User();
                        
                        newUser.setEmail(email);
                        newUser.setUsername(username);
                        userService.registerUser(newUser);
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    });
        }
        this.setAlwaysUseDefaultTargetUrl(true);

        // JWT TOKEN LOGIC
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Extract necessary attributes
        String email = (String) attributes.get("email");
        System.out.println("OAuth2LoginSuccessHandler: " + username + " : " + email);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>(oauth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList()));
        
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Create UserDetailsImpl instance
        UserDetailsImpl userDetails = new UserDetailsImpl(
                username,
                null,
                oauth2User.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                        .collect(Collectors.toList())
        );

        // Generate JWT token
        String jwtToken = jwtUtil.generateTokenFromUsername(userDetails);
        
     // Create HttpOnly cookie for JWT
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
            .httpOnly(true)
            .secure(false) // set true in production with https
            .sameSite("Lax")
            .path("/")
            .maxAge(24 * 60 * 60)  // 1 day lifetime
            .build();

        // Set the cookie in the response
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // Redirect to the frontend with the JWT token
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", jwtToken)
                .build().toUriString();
        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}