package com.prince.currencyexchange.entity;

import java.io.Serializable;
import java.util.Objects;

public class CurrencyRateId implements Serializable {

    private String currencyCodeFrom;
    private String currencyCodeTo;

    public CurrencyRateId() {
    }

    public CurrencyRateId(String currencyCodeFrom, String currencyCodeTo) {
        this.currencyCodeFrom = currencyCodeFrom;
        this.currencyCodeTo = currencyCodeTo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CurrencyRateId that)) {
            return false;
        }
        return Objects.equals(currencyCodeFrom, that.currencyCodeFrom)
                && Objects.equals(currencyCodeTo, that.currencyCodeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCodeFrom, currencyCodeTo);
    }
}
