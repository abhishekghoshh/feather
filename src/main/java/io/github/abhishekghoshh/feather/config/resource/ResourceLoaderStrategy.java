package io.github.abhishekghoshh.feather.config.resource;

import java.io.InputStream;

public interface ResourceLoaderStrategy {
    boolean supports(String path);
    InputStream load(String path) throws Exception;
}