package com.prince.currencyexchange;

import com.prince.currencyexchange.dto.ExchangeRateResponse;
import com.prince.currencyexchange.entity.Currency;
import com.prince.currencyexchange.entity.CurrencyRate;
import com.prince.currencyexchange.exception.CurrencyNotFoundException;
import com.prince.currencyexchange.exception.ExchangeRateNotFoundException;
import com.prince.currencyexchange.repository.CurrencyRateRepository;
import com.prince.currencyexchange.repository.CurrencyRepository;
import com.prince.currencyexchange.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {
    private CurrencyRepository currencyRepository;
    private CurrencyRateRepository currencyRateRepository;
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        currencyRateRepository = mock(CurrencyRateRepository.class);
        currencyService = new CurrencyService(currencyRepository, currencyRateRepository);
        for (String c : List.of("INR", "USD", "CAD", "EUR", "AUD", "AED")) {
            when(currencyRepository.existsByCurrencyCode(c)).thenReturn(true);
        }
    }

    @Test
    void directRateReturned() {
        CurrencyRate rate = mock(CurrencyRate.class);
        when(rate.getExchangeRate()).thenReturn(new BigDecimal("80.08"));
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("USD", "INR")).thenReturn(Optional.of(rate));

        ExchangeRateResponse response = currencyService.getExchangeRate("USD", "INR");
        assertEquals(new BigDecimal("80.08"), response.exchangeRate());
    }

    @Test
    void inverseRateReturnedRoundedHalfUp() {
        CurrencyRate reverse = mock(CurrencyRate.class);
        when(reverse.getExchangeRate()).thenReturn(new BigDecimal("80.08"));
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("INR", "USD")).thenReturn(Optional.empty());
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("USD", "INR")).thenReturn(Optional.of(reverse));

        ExchangeRateResponse response = currencyService.getExchangeRate("INR", "USD");
        assertEquals(new BigDecimal("0.0125"), response.exchangeRate());
    }

    @Test
    void sameCurrencyReturnsOne() {
        assertEquals(new BigDecimal("1.0"), currencyService.getExchangeRate("USD", "USD").exchangeRate());
    }

    @Test
    void lowercaseInputIsNormalized() {
        CurrencyRate rate = mock(CurrencyRate.class);
        when(rate.getExchangeRate()).thenReturn(new BigDecimal("80.08"));
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("USD", "INR")).thenReturn(Optional.of(rate));
        assertEquals("USD", currencyService.getExchangeRate("usd", "inr").fromCurrencyCode());
    }

    @Test
    void invalidCurrencyThrows() {
        assertThrows(CurrencyNotFoundException.class, () -> currencyService.getExchangeRate("XXX", "INR"));
    }

    @Test
    void missingRateThrows() {
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("CAD", "AED")).thenReturn(Optional.empty());
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("AED", "CAD")).thenReturn(Optional.empty());
        assertThrows(ExchangeRateNotFoundException.class, () -> currencyService.getExchangeRate("CAD", "AED"));
    }
}
