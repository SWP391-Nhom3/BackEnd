package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.*;
import com.anhduc.mevabe.dto.response.AuthenticationResponse;
import com.anhduc.mevabe.dto.response.IntrospectResponse;
import com.anhduc.mevabe.dto.response.UserResponse;
import com.anhduc.mevabe.entity.InvalidatedToken;
import com.anhduc.mevabe.entity.Role;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.repository.InvalidatedTokenRepository;
import com.anhduc.mevabe.repository.RoleRepository;
import com.anhduc.mevabe.repository.httpclient.OutboundIdentityClient;
import com.anhduc.mevabe.repository.UserRepository;
import com.anhduc.mevabe.repository.httpclient.OutboundUserClient;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;
    RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @NonFinal // dont inject in constructor
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.tokenExpiryTime}")
    protected long tokenExpiryTime;

    @NonFinal
    @Value("${jwt.refreshTokenExpiration}")
    protected long refreshTokenExpiration;

    @NonFinal
    @Value("${oauth2.google.client-id}")
    private String CLIENT_ID;

    @NonFinal
    @Value("${oauth2.google.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${oauth2.google.redirect-uri}")
    protected String REDIRECT_URI;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .user(userResponse)
                .build();

    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // claims standard
                .subject(user.getEmail())  // dai dien thong tin dang nhap
                .issuer("mevabe.com") // xác định cái token được issuer từ ai , thuong la domain service
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(tokenExpiryTime, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();// claim is data in body call is claims

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload); //gen token need to header and payload
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error while generating token", e);
            throw new RuntimeException(e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getAccessToken();
        boolean inValid  = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            inValid = false;
        }
        return IntrospectResponse.builder().valid(inValid).build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role ->
                    {
                        stringJoiner.add("ROLE_" + role.getName());
                        if (!CollectionUtils.isEmpty(role.getPermissions()))
                            role.getPermissions().forEach(
                                    permission -> stringJoiner.add(permission.getName())
                            );
                    }

            );
        }
        return stringJoiner.toString();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
       try {
           var signToken = verifyToken(request.getToken(), true);

           String jit = signToken.getJWTClaimsSet().getJWTID();
           Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

           InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                   .id(UUID.fromString(jit))
                   .expiryTime(expiryTime).build();

           invalidatedTokenRepository.save(invalidatedToken);
       } catch (AppException e) {
           log.error("Token already expired", e);
       }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(signerKey);

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = isRefresh
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                        .plus(refreshTokenExpiration, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier); // return true or false

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(UUID.fromString(signedJWT.getJWTClaimsSet().getJWTID())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(UUID.fromString(jit))
                .expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .build();
    }

    public AuthenticationResponse outboundLoginGoogle(String code) {
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType("authorization_code")
                .build());
        log.info("token: {}", response);
        var userInfo = outboundUserClient.getUserInfo("json",response.getAccessToken());

        log.info("userInfo: {}", userInfo);
        Set<Role> roles = new HashSet<>();
        Role roleDefault = roleRepository.findByName("MEMBER").orElseThrow(
                () -> new AppException(ErrorCode.ROLE_NOT_FOUND)
        );
        roles.add(roleDefault);
        var user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(
                () -> userRepository.save(User.builder()
                                .email(userInfo.getEmail())
                                .firstName(userInfo.getGiveName())
                                .lastName(userInfo.getFamilyName())
                                .roles(roles)
                        .build())
        );

        var token = generateToken(user);
        return AuthenticationResponse.builder().accessToken(token).build();
    }
}
