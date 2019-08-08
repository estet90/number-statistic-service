package ru.kononov.numberstatisticservice.api.util;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public class PropertyResolver {

    private static final Logger logger = Logger.getLogger(PropertyResolver.class.getName());
    private final Properties properties;

    public PropertyResolver() {
        this.properties = new Properties();
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(requireNonNull(inputStream));
        } catch (IOException | NullPointerException e) {
            logger.log(Level.WARNING, "PropertyResolver.init.thrown ", e);
            throw new RuntimeException(e);
        }
    }

    public String getContextPath() {
        try {
            return getProperty("server.path");
        } catch (Exception e) {
            logger.log(Level.WARNING, "PropertyResolver.getContextPath.thrown ", e);
            throw e;
        }
    }

    public int getPort() {
        try {
            return Integer.valueOf(getProperty("server.port"));
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "PropertyResolver.getPort.thrown ", e);
            throw e;
        }
    }

    private String getProperty(String key) {
        var value = requireNonNull(properties.getProperty(key));
        logger.info(String.format("PropertyResolver.getProperty.out key=%s value=%s", key, value));
        return value;
    }

}
