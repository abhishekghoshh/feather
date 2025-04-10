package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ErrorCode;
import io.github.abhishekghoshh.crud.dto.ResourceExceptionType;
import io.github.abhishekghoshh.crud.dto.ResourceType;

public class DuplicateResourceException extends ResourceException {
    public DuplicateResourceException(ResourceType resourceType, String message) {
        super(resourceType,
                ResourceExceptionType.RESOURCE_ALREADY_EXISTING,
                ErrorCode.BadRequest,
                message);
    }
}
