package com.anhduc.mevabe.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Email(message = "EMAIL_INCORRECT_FORMAT")
    String email;
    @Size(min = 8, message = "PASSWORD_INCORRECT_FORMAT")
    String password;
//    String firstName;
//    String lastName;
//    private String phone;
//    private int pointsBalance = 0;
//    private String emailVerificationToken;
}
