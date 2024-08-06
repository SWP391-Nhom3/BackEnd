package com.anhduc.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserRequestDto {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String avatar;
    private int pointsBalance;
}
