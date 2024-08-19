package com.anhduc.mevabe.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_MESSAGE_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    EMAIL_INCORRECT_FORMAT(1003, "Email invalid format", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1004, "Email already exists", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT_FORMAT(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INCORRECT_PASSWORD(1008, "Incorrect password", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1009, "Role not found", HttpStatus.NOT_FOUND),
    PASSWORD_EXISTED(1010, "Password already existed", HttpStatus.CONFLICT),
    PRODUCT_NOT_FOUND(1011, "Product not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(1012, "Order not found", HttpStatus.NOT_FOUND),
    VOUCHER_INVALID(1013, "Voucher is not valid.", HttpStatus.NOT_FOUND),
    ;

    private final int code;
    private final String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
