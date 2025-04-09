package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ErrorCode;
import io.github.abhishekghoshh.crud.dto.ResourceExceptionType;
import io.github.abhishekghoshh.crud.dto.ResourceType;
import io.github.abhishekghoshh.crud.exception.ResourceException;

public class ResourceNotFoundException extends ResourceException {
    public ResourceNotFoundException(ResourceType resourceType, long id, String resourceName) {
        super(resourceType, ResourceExceptionType.RESOURCE_NOT_FOUND, ErrorCode.NotFound,
                resourceName + " not found for the id : " + id);
    }

    public ResourceNotFoundException(ResourceType resourceType, long id) {
        this(resourceType, id, resourceType.toString());
    }
}
