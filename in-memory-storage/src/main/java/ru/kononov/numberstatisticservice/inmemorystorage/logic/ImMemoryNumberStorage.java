package ru.kononov.numberstatisticservice.inmemorystorage.logic;

import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

public class ImMemoryNumberStorage implements NumberStorage {

    List<BigDecimal> numbers = Collections.synchronizedList(new ArrayList<>());
    volatile BigDecimal min = null;
    volatile BigDecimal max = null;
    volatile BigDecimal sum = null;

    @Override
    public synchronized void add(BigDecimal number) {
        Objects.requireNonNull(number);
        numbers.add(number);
        if (isNull(min) || number.compareTo(min) < 0) {
            min = number;
        }
        if (isNull(max) || number.compareTo(max) > 0) {
            max = number;
        }
        sum = isNull(sum) ? number : sum.add(number);
    }

    @Override
    public BigDecimal min() {
        return getResultWithCheck(() -> min);
    }

    @Override
    public BigDecimal max() {
        return getResultWithCheck(() -> max);
    }

    @Override
    public BigDecimal average() {
        return getResultWithCheck(() -> sum.divide(BigDecimal.valueOf(numbers.size()), MathContext.DECIMAL32));
    }

    private BigDecimal getResultWithCheck(Supplier<BigDecimal> resultSupplier) {
        if (numbers.size() == 0) {
            return null;
        }
        return resultSupplier.get();
    }
}
