package com.anhduc.mevabe.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCode {
    INVALID_MESSAGE_KEY(1001, "Invalid message key"),
    USER_EXISTED(1002, "User existed"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
    EMAIL_INCORRECT_FORMAT(1003, "Email invalid format"),
    PASSWORD_INCORRECT_FORMAT(1004, "Password must be at least 8 characters"),
    USER_NOT_EXISTED(1005, "User not existed"),
    UNAUTHENTICATED(1006, "Unauthenticated"),
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
