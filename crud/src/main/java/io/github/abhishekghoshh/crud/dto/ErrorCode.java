package io.github.abhishekghoshh.crud.dto;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NotFound(404),
    BadRequest(400),
    PreconditionFailed(412),
    Unprocessable(422),
    InternalServerError(500);

    private final int id;

    ErrorCode(int id) {
        this.id = id;
    }

    public static ErrorCode fromId(int id) {
        for (ErrorCode code : values()) {
            if (code.id == id) {
                return code;
            }
        }
        throw new IllegalArgumentException("Invalid ErrorCode id: " + id);
    }
}
