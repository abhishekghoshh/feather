package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ErrorCode;
import io.github.abhishekghoshh.crud.dto.ResourceExceptionType;
import io.github.abhishekghoshh.crud.dto.ResourceType;
import io.github.abhishekghoshh.crud.exception.ResourceException;

public class InvalidUidException extends ResourceException {
    public InvalidUidException(String uid, ResourceType resourceType) {
        super(resourceType,
                ResourceExceptionType.INVALID_UID_FORMAT,
                ErrorCode.BadRequest,
                uid + " is not a valid uid"
        );
    }
}
