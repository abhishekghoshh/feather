package io.github.abhishekghoshh.feather.config.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileSystemLoader implements ResourceLoaderStrategy {
    @Override
    public boolean supports(String path) {
        return path.startsWith("/");
    }

    @Override
    public InputStream load(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }
}