package ru.kononov.numberstatisticservice.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class PropertyResolver {

    private static final Logger logger = LogManager.getLogger(PropertyResolver.class);
    private final Properties properties;

    public PropertyResolver(String propertyFileName) {
        this.properties = new Properties();
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName)) {
            properties.load(requireNonNull(inputStream));
        } catch (Exception e) {
            logger.error("PropertyResolver.init.thrown ", e);
            throw new RuntimeException(e);
        }
    }

    public String getContextPath() {
        return getProperty("server.path", Function.identity());
    }

    public int getPort() {
        return getProperty("server.port", Integer::valueOf);
    }

    private <T> T getProperty(String key, Function<String, T> transformer) {
        try {
            var value = requireNonNull(properties.getProperty(key));
            logger.info("PropertyResolver.getProperty.out key={} value={}", key, value);
            return transformer.apply(value);
        } catch (Exception e) {
            logger.error("PropertyResolver.getProperty.thrown key={}", key, e);
            throw e;
        }
    }

}
