package ru.kononov.numberstatisticservice.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

public class PropertyResolver {

    private static final Logger logger = LogManager.getLogger(PropertyResolver.class);
    private final Properties properties;

    public PropertyResolver() {
        this.properties = new Properties();
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(requireNonNull(inputStream));
        } catch (IOException | NullPointerException e) {
            logger.error("PropertyResolver.init.thrown ", e);
            throw new RuntimeException(e);
        }
    }

    public String getContextPath() {
        try {
            return getProperty("server.path");
        } catch (Exception e) {
            logger.error("PropertyResolver.getContextPath.thrown ", e);
            throw e;
        }
    }

    public int getPort() {
        try {
            return Integer.valueOf(getProperty("server.port"));
        } catch (NumberFormatException e) {
            logger.error("PropertyResolver.getPort.thrown ", e);
            throw e;
        }
    }

    private String getProperty(String key) {
        var value = requireNonNull(properties.getProperty(key));
        logger.info("PropertyResolver.getProperty.out key={} value={}", key, value);
        return value;
    }

}
