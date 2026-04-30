package com.prince.currencyexchange.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@IdClass(CurrencyRateId.class)
@Table(name = "currency_rate")
public class CurrencyRate {

    @Id
    @Column(name = "currency_code_from")
    private String currencyCodeFrom;

    @Id
    @Column(name = "currency_code_to")
    private String currencyCodeTo;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    public String getCurrencyCodeFrom() {
        return currencyCodeFrom;
    }

    public String getCurrencyCodeTo() {
        return currencyCodeTo;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
}
