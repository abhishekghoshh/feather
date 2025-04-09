package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ErrorCode;
import lombok.Getter;

@Getter
public class InternalServerException extends Exception {

    private final ErrorCode status;

    public InternalServerException(ErrorCode status, String message) {
        super(message);
        this.status = status;
    }

    public static InternalServerException INTERNAL_SERVER_ERROR() {
        return new InternalServerException(ErrorCode.InternalServerError, "internal server error");
    }
}