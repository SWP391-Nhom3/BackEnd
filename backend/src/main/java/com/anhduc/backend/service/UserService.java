package com.anhduc.backend.service;


import com.anhduc.backend.dto.request.UserRequestDto;
import com.anhduc.backend.dto.response.UserResponseDto;
import com.anhduc.backend.entity.User;
import com.anhduc.backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public interface UserService {
    void save(UserRequestDto dto);
    UserResponseDto findById(UUID id);
    List<UserResponseDto> findAll();
    void deleteById(UUID id);
    void update(UserRequestDto dto);
    void register(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;
    boolean verify(String verificationCode);
}

@Service
@Transactional
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public void register(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationCode(UUID.randomUUID().toString());
        user.setEnabled(false);
        userRepository.save(user);

        String verificationLink = siteURL + "/verify?code=" + user.getVerificationCode();
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationLink);
    }

    public boolean verify(String verificationCode) {
        Optional<User> userOptional = userRepository.findByVerificationCode(verificationCode);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void save(UserRequestDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        userRepository.save(user);
    }

    @Override
    public UserResponseDto findById(UUID id) {
        User user =  userRepository.findById(id).orElseThrow(() -> new NoResultException());
        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        User user =  userRepository.findById(id).orElseThrow(() -> new NoResultException());
        userRepository.delete(user);
    }

    @Override
    public void update(UserRequestDto dto) {

    }

    private UserResponseDto convertToDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }
}
