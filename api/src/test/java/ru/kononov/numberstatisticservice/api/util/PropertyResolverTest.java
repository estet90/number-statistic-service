package ru.kononov.numberstatisticservice.api.util;

import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyResolverTest {

    @Test
    void getContextPath() {
        var propertyResolver = new PropertyResolver("application.properties");

        var result = propertyResolver.getContextPath();

        assertThat(result).isEqualTo("/number-statistic/service/api/v1");
    }

    @Test
    void getPort() {
        var propertyResolver = new PropertyResolver("application.properties");

        var result = propertyResolver.getPort();

        assertThat(result).isEqualTo(8000);
    }

    @Test
    void getPortWrongFormat() {
        var propertyResolver = new PropertyResolver("application-wrong-port-format.properties");

        assertThrows(NumberFormatException.class, propertyResolver::getPort);
    }

    @Test
    void propertyFileNotFound() {
        var e = assertThrows(RuntimeException.class, () -> new PropertyResolver("application1.properties"));

        assertThat(e.getCause()).isOfAnyClassIn(NullPointerException.class);
    }

}
