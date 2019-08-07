package ru.kononov.numberstatisticservice.storageapi.logic;

import java.math.BigDecimal;

public interface NumberStorage {

    void add(BigDecimal number);
    BigDecimal min();
    BigDecimal max();
    BigDecimal average();

}
