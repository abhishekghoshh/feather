package io.github.abhishekghoshh.feather.config.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClasspathLoader implements ResourceLoaderStrategy {
    @Override
    public boolean supports(String path) {
        return getClass().getClassLoader().getResource(path) != null;
    }

    @Override
    public InputStream load(String path) throws FileNotFoundException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null)
            throw new FileNotFoundException("Resource not found in classpath: " + path);
        return is;
    }
}