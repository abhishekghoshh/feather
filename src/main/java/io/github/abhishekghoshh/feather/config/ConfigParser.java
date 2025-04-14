package io.github.abhishekghoshh.feather.config;

import io.github.abhishekghoshh.feather.config.resource.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser {

    private static final Logger logger = LoggerFactory.getLogger(ConfigParser.class);


    private final ResourceLoader loader;
    private final Config config;
    private static final String DEFAULT_PATH = "classpath:application.yaml";
    private static final Pattern ENV_VAR_PATTERN = Pattern.compile("\\$\\{(\\w+)}");
    private static final Pattern ENV_VAR_WITH_DEFAULT_PATTERN = Pattern.compile("\\$\\{(\\w+):([^}]+)}");

    public ConfigParser() {
        this.loader = ResourceLoader.getInstance();
        this.config = Config.getInstance();
    }


    public Map<String, Object> load(String path) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        try (InputStream in = loader.load(path)) {
            Map<String, Object> map = yaml.load(in);
            return processMap(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML file: " + path, e);
        }
    }

    private Map<String, Object> processMap(Map<String, Object> map) {
        Map<String, Object> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            newMap.put(entry.getKey(), processObject(entry.getValue()));
        }
        return newMap;
    }


    private Object processObject(Object obj) {
        if (obj instanceof Map<?, ?>) {
            Map<String, Object> newMap = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                newMap.put(entry.getKey().toString(), processObject(entry.getValue()));
            }
            return newMap;
        } else if (obj instanceof List<?>) {
            List<Object> newList = new ArrayList<>();
            for (Object item : (List<?>) obj) {
                newList.add(processObject(item));
            }
            return newList;
        } else {
            return resolvePlaceholders(obj);
        }
    }

    private Object resolvePlaceholders(Object value) {
        if (value instanceof String str) {
            Matcher matcherWithDefault = ENV_VAR_WITH_DEFAULT_PATTERN.matcher(str);
            if (matcherWithDefault.find()) {
                String envKey = matcherWithDefault.group(1);
                String defaultVal = matcherWithDefault.group(2);
                return System.getenv().getOrDefault(envKey, defaultVal);
            }

            Matcher matcher = ENV_VAR_PATTERN.matcher(str);
            if (matcher.find()) {
                String envKey = matcher.group(1);
                String envVal = System.getenv(envKey);
                if (envVal != null) {
                    return envVal;
                } else {
                    throw new IllegalArgumentException("Environment variable '" + envKey + "' is not set");
                }
            }
        }
        return value;
    }


    public static Object getValueByPath(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;

        for (String part : parts) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<?, ?>) current).get(part);
        }
        return current;
    }


}
