package io.github.abhishekghoshh.crud.dto;


import io.github.abhishekghoshh.crud.exception.ResourceException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceError {

    private String resourceType;
    private String exceptionType;
    private String message;

    public ResourceError() {
    }

    public ResourceError(String resourceType, String exceptionType, String message) {
        this.resourceType = resourceType;
        this.exceptionType = exceptionType;
        this.message = message;
    }

    public static ResourceError create(ResourceException resourceException) {
        return new ResourceError(
                String.valueOf(resourceException.getResourceType()),
                String.valueOf(resourceException.getExceptionType()),
                resourceException.getMessage()
        );
    }

}
