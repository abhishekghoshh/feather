package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ResourceType;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(long id) {
        super(ResourceType.USER, id);
    }
}

