package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ErrorCode;
import io.github.abhishekghoshh.crud.dto.ResourceExceptionType;
import io.github.abhishekghoshh.crud.dto.ResourceType;

public class BlogNotFoundException extends ResourceException {
    public BlogNotFoundException(String message) {
        super(ResourceType.BLOG,
                ResourceExceptionType.RESOURCE_NOT_FOUND,
                ErrorCode.NotFound,
                message
        );
    }
}
