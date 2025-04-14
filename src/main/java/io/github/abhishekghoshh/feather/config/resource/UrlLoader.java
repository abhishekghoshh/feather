package io.github.abhishekghoshh.feather.config.resource;

import java.io.InputStream;
import java.net.URI;

public class UrlLoader implements ResourceLoaderStrategy {
    @Override
    public boolean supports(String path) {
        try {
            new URI(path).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public InputStream load(String path) throws Exception {
        return new URI(path).toURL().openStream();
    }
}