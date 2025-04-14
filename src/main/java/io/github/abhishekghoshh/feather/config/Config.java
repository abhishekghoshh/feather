package io.github.abhishekghoshh.feather.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Config {

    private static final Config INSTANCE = new Config();

    public static Config getInstance() {
        return INSTANCE;
    }

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private final Map<String, Object> config = new HashMap<>();

    private Config() {
    }

    public void addConfig(Map<String, Object> config) {
        this.config.putAll(config);
    }


    public Object get(String key, Object defaultValue) {
        String[] keys = key.split("\\.");
        Object current = config;
        for (String k : keys) {
            if (current instanceof Map<?, ?> map && map.containsKey(k)) {
                current = map.get(k);
            } else {
                if (defaultValue != null) return defaultValue;
                throw new NoSuchElementException("Key '" + key + "' not found in configuration");
            }
        }
        return current;
    }

    public Object get(String key) {
        return get(key, null);
    }


    public String getString(String key, String defaultValue) {
        Object val = get(key, defaultValue);
        if (!(val instanceof String)) {
            throw new IllegalArgumentException("Value for key '" + key + "' is not a string");
        }
        return (String) val;
    }

    public List<?> getList(String key, List<?> defaultValue) {
        Object val = get(key, defaultValue);
        if (!(val instanceof List<?>)) {
            throw new IllegalArgumentException("Value for key '" + key + "' is not a list");
        }
        return (List<?>) val;
    }

    public Map<String, Object> getMap(String key, Map<String, Object> defaultValue) {
        Object val = get(key, defaultValue);
        if (!(val instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Value for key '" + key + "' is not a map");
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue());
        }
        return result;
    }
}
