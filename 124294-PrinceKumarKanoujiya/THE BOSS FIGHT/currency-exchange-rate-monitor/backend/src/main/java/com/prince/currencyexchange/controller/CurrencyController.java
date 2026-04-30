package com.prince.currencyexchange.controller;

import com.prince.currencyexchange.dto.CurrencyResponse;
import com.prince.currencyexchange.dto.ExchangeRateResponse;
import com.prince.currencyexchange.service.CurrencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/currency")
    public Map<String, CurrencyResponse> currencies() {
        return currencyService.getAllCurrencies();
    }

    @GetMapping("/exchange-rate/{fromCur}/{toCur}")
    public ExchangeRateResponse rate(@PathVariable String fromCur, @PathVariable String toCur) {
        return currencyService.getExchangeRate(fromCur, toCur);
    }
}
