package com.anhduc.backend.security;

import com.anhduc.backend.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2User extends DefaultOAuth2User {

    private User user;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        super(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()), attributes, "sub");
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}
