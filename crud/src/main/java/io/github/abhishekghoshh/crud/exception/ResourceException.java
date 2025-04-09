package io.github.abhishekghoshh.crud.exception;


import io.github.abhishekghoshh.crud.dto.ErrorCode;
import io.github.abhishekghoshh.crud.dto.ResourceExceptionType;
import io.github.abhishekghoshh.crud.dto.ResourceType;
import lombok.Getter;

@Getter
public class ResourceException extends Exception {

    private final ResourceType resourceType;
    private final ResourceExceptionType exceptionType;
    private final ErrorCode status;

    public ResourceException(ResourceType resourceType, ResourceExceptionType exceptionType, ErrorCode status, String message) {
        super(message);
        this.resourceType = resourceType;
        this.exceptionType = exceptionType;
        this.status = status;
    }

}

