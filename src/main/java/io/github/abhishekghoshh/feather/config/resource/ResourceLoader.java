package io.github.abhishekghoshh.feather.config.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class ResourceLoader {
    private static final ResourceLoader INSTANCE = new ResourceLoader();

    public static ResourceLoader getInstance() {
        return INSTANCE;
    }

    private final Map<String, ResourceLoaderStrategy> strategies;

    private ResourceLoader() {
        strategies = Map.of(
                "file", new FileSystemLoader(),
                "url", new UrlLoader(),
                "classPath", new ClasspathLoader()
        );
    }

    public InputStream load(String filePath) throws Exception {
        for (Map.Entry<String, ResourceLoaderStrategy> entry : strategies.entrySet()) {
            String key = entry.getKey();
            ResourceLoaderStrategy strategy = entry.getValue();
            if (strategy.supports(filePath)) {
                return strategy.load(filePath);
            }
        }
        throw new FileNotFoundException("No suitable loader found for: " + filePath);
    }

}
