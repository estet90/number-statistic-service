package ru.kononov.numberstatisticservice.inmemorystorage.logic;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImMemoryNumberStorageTest {

    private final List<BigDecimal> numbers = List.of(
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4)
    );

    @Test
    void add() {
        var storage = new ImMemoryNumberStorage();

        assertDoesNotThrow(() -> numbers.forEach(storage::add));

        assertThat(storage.getCount()).isEqualTo(4);
    }

    @Test
    void addException() {
        var storage = new ImMemoryNumberStorage();

        assertThrows(NullPointerException.class, () -> storage.add(null));
    }

    @Test
    void min() {
        var storage = new ImMemoryNumberStorage();

        assertDoesNotThrow(() -> numbers.forEach(storage::add));
        var min = storage.min();

        assertThat(min).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    void max() {
        var storage = new ImMemoryNumberStorage();

        assertDoesNotThrow(() -> numbers.forEach(storage::add));
        var max = storage.max();

        assertThat(max).isEqualTo(BigDecimal.valueOf(4));
    }

    @Test
    void average() {
        var storage = new ImMemoryNumberStorage();

        assertDoesNotThrow(() -> numbers.forEach(storage::add));
        var average = storage.average();

        assertThat(average).isEqualTo(new BigDecimal("2.5"));
    }

}
