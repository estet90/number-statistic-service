package ru.kononov.numberstatisticservice.inmemorystorage.logic;

import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.isNull;

public class ImMemoryNumberStorage implements NumberStorage {

    private static CopyOnWriteArrayList<BigDecimal> numbers = new CopyOnWriteArrayList<>();
    private static volatile BigDecimal min = null;
    private static volatile BigDecimal max = null;

    @Override
    public void add(BigDecimal number) {
        numbers.add(number);
        if (isNull(min) || number.compareTo(min) < 0) {
            min = number;
        }
        if (isNull(max) || number.compareTo(max) > 0) {
            max = number;
        }
    }

    @Override
    public BigDecimal min() {
        if (numbers.size() == 0) {
            return null;
        }
        return min;
    }

    @Override
    public BigDecimal max() {
        if (numbers.size() == 0) {
            return null;
        }
        return max;
    }

    @Override
    public BigDecimal average() {
        if (numbers.size() == 0) {
            return null;
        }
        var sum = numbers.stream().reduce(BigDecimal::add).get();
        return sum.divide(BigDecimal.valueOf(numbers.size()), MathContext.DECIMAL32);
    }
}
