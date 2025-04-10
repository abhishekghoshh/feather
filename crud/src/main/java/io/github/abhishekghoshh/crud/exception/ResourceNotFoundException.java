package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ErrorCode;
import io.github.abhishekghoshh.crud.dto.ResourceExceptionType;
import io.github.abhishekghoshh.crud.dto.ResourceType;

public class ResourceNotFoundException extends ResourceException {
    public ResourceNotFoundException(ResourceType resourceType, long id) {
        super(resourceType, ResourceExceptionType.RESOURCE_NOT_FOUND, ErrorCode.NotFound,
                ResourceType.USER.toString().toLowerCase() + " not found for the id : " + id);
    }
}
