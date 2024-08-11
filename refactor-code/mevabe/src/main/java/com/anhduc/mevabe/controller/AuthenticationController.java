package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.AuthenticationRequest;
import com.anhduc.mevabe.dto.request.IntrospectRequest;
import com.anhduc.mevabe.dto.request.LogoutRequest;
import com.anhduc.mevabe.dto.request.RefreshRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.AuthenticationResponse;
import com.anhduc.mevabe.dto.response.IntrospectResponse;
import com.anhduc.mevabe.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/outbound/login-google")
    public ApiResponse<AuthenticationResponse> loginGoogle(@RequestParam("code") String code) {
        return ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationService.outboundLoginGoogle(code)).build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/logout")
    public  ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }
}
