package com.prince.currencyexchange.service;

import com.prince.currencyexchange.dto.CurrencyResponse;
import com.prince.currencyexchange.dto.ExchangeRateResponse;
import com.prince.currencyexchange.exception.CurrencyNotFoundException;
import com.prince.currencyexchange.exception.ExchangeRateNotFoundException;
import com.prince.currencyexchange.repository.CurrencyRateRepository;
import com.prince.currencyexchange.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

    public CurrencyService(CurrencyRepository currencyRepository, CurrencyRateRepository currencyRateRepository) {
        this.currencyRepository = currencyRepository;
        this.currencyRateRepository = currencyRateRepository;
    }

    public Map<String, CurrencyResponse> getAllCurrencies() {
        Map<String, CurrencyResponse> response = new LinkedHashMap<>();
        currencyRepository.findAll().forEach(currency -> response.put(
                currency.getCurrencyCode(),
                new CurrencyResponse(currency.getCountryName(), currency.getCurrencyCode(), currency.getCurrencyName())
        ));
        return response;
    }

    public ExchangeRateResponse getExchangeRate(String fromCur, String toCur) {
        String from = normalizeCurrencyCode(fromCur);
        String to = normalizeCurrencyCode(toCur);

        validateCurrencyExists(from);
        validateCurrencyExists(to);

        if (from.equals(to)) {
            return new ExchangeRateResponse(from, to, new BigDecimal("1.0"));
        }

        var directRate = currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo(from, to);
        if (directRate.isPresent()) {
            return new ExchangeRateResponse(from, to, directRate.get().getExchangeRate());
        }

        var reverseRate = currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo(to, from);
        if (reverseRate.isPresent()) {
            BigDecimal inverse = BigDecimal.ONE.divide(reverseRate.get().getExchangeRate(), 4, RoundingMode.HALF_UP);
            return new ExchangeRateResponse(from, to, inverse);
        }

        throw new ExchangeRateNotFoundException("Exchange rate not found for " + from + " to " + to);
    }

    public String normalizeCurrencyCode(String code) {
        if (code == null || code.isBlank()) {
            throw new CurrencyNotFoundException("Currency code is required");
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    public void validateCurrencyExists(String code) {
        Set<String> supported = Set.of("INR", "USD", "CAD", "EUR", "AUD", "AED");
        if (!supported.contains(code) || !currencyRepository.existsByCurrencyCode(code)) {
            throw new CurrencyNotFoundException("Unsupported currency code: " + code);
        }
    }
}
