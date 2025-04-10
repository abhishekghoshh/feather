package io.github.abhishekghoshh.crud.exception;

import io.github.abhishekghoshh.crud.dto.ResourceType;

public class BlogNotFoundException extends ResourceNotFoundException {
    public BlogNotFoundException(long id) {
        super(ResourceType.BLOG, id);
    }
}