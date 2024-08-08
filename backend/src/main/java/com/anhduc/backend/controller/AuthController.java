package com.anhduc.backend.controller;

import com.anhduc.backend.config.JwtTokenProvider;
import com.anhduc.backend.dto.request.LoginRequest;
import com.anhduc.backend.dto.response.ApiResponse;
import com.anhduc.backend.dto.response.JwtAuthenticationResponse;
import com.anhduc.backend.entity.User;
import com.anhduc.backend.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ApiResponse<Void> registerUser(@Valid @RequestBody User user, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String siteURL = request.getRequestURL().toString();
        userService.register(user, siteURL);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK)
                .message("Member registered successfully")
                .build();
    }

    @GetMapping("/verify/{code}")
    public ApiResponse<String> verify(@PathVariable String code) {
        if (userService.verify(code)) {
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK)
                    .message("Member verified successfully")
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK)
                    .message("Member Verification failed.")
                    .build();
        }
    }

//    @PostMapping("resend-verify")


    @PostMapping("/login")
    public ApiResponse<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ApiResponse.<JwtAuthenticationResponse>builder()
                .status(HttpStatus.OK)
                .data(new JwtAuthenticationResponse(jwt))
                .build();
    }

}
