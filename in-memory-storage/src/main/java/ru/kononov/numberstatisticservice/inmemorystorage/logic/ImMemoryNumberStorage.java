package ru.kononov.numberstatisticservice.inmemorystorage.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

public class ImMemoryNumberStorage implements NumberStorage {

    private static final Logger logger = LogManager.getLogger(ImMemoryNumberStorage.class);

    private volatile BigDecimal min = null;
    private volatile BigDecimal max = null;
    private volatile long count = 0;
    private volatile BigDecimal sum = null;

    @Override
    public synchronized void add(BigDecimal number) {
        Objects.requireNonNull(number);
        if (isNull(min) || number.compareTo(min) < 0) {
            min = number;
        }
        if (isNull(max) || number.compareTo(max) > 0) {
            max = number;
        }
        sum = isNull(sum) ? number : sum.add(number);
        count++;
        logger.debug("ImMemoryNumberStorage.add\n\tnumber={}\n\tsum={}\n\tcount={}", number, sum, count);
    }

    @Override
    public BigDecimal min() {
        var result = getResultWithCheck(() -> min);
        logger.debug("ImMemoryNumberStorage.min result={}", result);
        return result;
    }

    @Override
    public BigDecimal max() {
        var result = getResultWithCheck(() -> max);
        logger.debug("ImMemoryNumberStorage.max result={}", result);
        return result;
    }

    @Override
    public BigDecimal average() {
        var result = getResultWithCheck(() -> sum.divide(BigDecimal.valueOf(count), MathContext.DECIMAL32));
        logger.debug("ImMemoryNumberStorage.average result={}", result);
        return result;
    }

    long getCount() {
        return this.count;
    }

    BigDecimal getSum() {
        return this.sum;
    }

    private BigDecimal getResultWithCheck(Supplier<BigDecimal> resultSupplier) {
        if (count == 0) {
            return null;
        }
        return resultSupplier.get();
    }
}
