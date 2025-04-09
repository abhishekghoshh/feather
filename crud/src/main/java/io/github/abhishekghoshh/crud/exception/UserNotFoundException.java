package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ErrorCode;
import io.github.abhishekghoshh.crud.dto.ResourceExceptionType;
import io.github.abhishekghoshh.crud.dto.ResourceType;
import io.github.abhishekghoshh.crud.exception.ResourceException;

public class UserNotFoundException extends ResourceException {
    public UserNotFoundException(String message) {
        super(ResourceType.USER, ResourceExceptionType.RESOURCE_NOT_FOUND, ErrorCode.NotFound, message);
    }
}
