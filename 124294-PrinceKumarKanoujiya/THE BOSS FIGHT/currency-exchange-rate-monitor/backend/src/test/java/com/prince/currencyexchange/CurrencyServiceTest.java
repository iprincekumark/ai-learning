package com.prince.currencyexchange;

import com.prince.currencyexchange.dto.CurrencyResponse;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyServiceTest {

    private CurrencyRepository currencyRepository;
    private CurrencyRateRepository currencyRateRepository;
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        currencyRateRepository = mock(CurrencyRateRepository.class);
        currencyService = new CurrencyService(currencyRepository, currencyRateRepository);

        for (String code : List.of("INR", "USD", "CAD", "EUR", "AUD", "AED")) {
            when(currencyRepository.existsByCurrencyCode(code)).thenReturn(true);
        }
    }

    @Test
    void getExchangeRate_shouldReturnDirectRate_forUsdToInr() {
        CurrencyRate rate = mock(CurrencyRate.class);
        when(rate.getExchangeRate()).thenReturn(new BigDecimal("80.08"));
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("USD", "INR"))
                .thenReturn(Optional.of(rate));

        ExchangeRateResponse response = currencyService.getExchangeRate("USD", "INR");

        assertEquals(new BigDecimal("80.08"), response.exchangeRate());
    }

    @Test
    void getExchangeRate_shouldReturnInverseRate_forInrToUsd() {
        CurrencyRate reverseRate = mock(CurrencyRate.class);
        when(reverseRate.getExchangeRate()).thenReturn(new BigDecimal("80.08"));
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("INR", "USD"))
                .thenReturn(Optional.empty());
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("USD", "INR"))
                .thenReturn(Optional.of(reverseRate));

        ExchangeRateResponse response = currencyService.getExchangeRate("INR", "USD");

        assertEquals(new BigDecimal("0.0125"), response.exchangeRate());
    }

    @Test
    void getExchangeRate_shouldReturnOne_forSameCurrency() {
        ExchangeRateResponse response = currencyService.getExchangeRate("USD", "USD");
        assertEquals(new BigDecimal("1.0"), response.exchangeRate());
    }

    @Test
    void getExchangeRate_shouldNormalizeLowercaseInput() {
        CurrencyRate rate = mock(CurrencyRate.class);
        when(rate.getExchangeRate()).thenReturn(new BigDecimal("80.08"));
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("USD", "INR"))
                .thenReturn(Optional.of(rate));

        ExchangeRateResponse response = currencyService.getExchangeRate("usd", "inr");

        assertEquals("USD", response.fromCurrencyCode());
        assertEquals("INR", response.toCurrencyCode());
    }

    @Test
    void getExchangeRate_shouldReturnDirectRate_forUsdToCad() {
        CurrencyRate rate = mock(CurrencyRate.class);
        when(rate.getExchangeRate()).thenReturn(new BigDecimal("1.36"));
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("USD", "CAD"))
                .thenReturn(Optional.of(rate));

        ExchangeRateResponse response = currencyService.getExchangeRate("USD", "CAD");

        assertEquals(new BigDecimal("1.36"), response.exchangeRate());
    }

    @Test
    void getExchangeRate_shouldThrowCurrencyNotFound_forInvalidFromCurrency() {
        assertThrows(CurrencyNotFoundException.class, () -> currencyService.getExchangeRate("XXX", "INR"));
    }

    @Test
    void getExchangeRate_shouldThrowCurrencyNotFound_forInvalidToCurrency() {
        assertThrows(CurrencyNotFoundException.class, () -> currencyService.getExchangeRate("USD", "XXX"));
    }

    @Test
    void getExchangeRate_shouldThrowExchangeRateNotFound_forMissingCadToAedRate() {
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("CAD", "AED"))
                .thenReturn(Optional.empty());
        when(currencyRateRepository.findByCurrencyCodeFromAndCurrencyCodeTo("AED", "CAD"))
                .thenReturn(Optional.empty());

        assertThrows(ExchangeRateNotFoundException.class, () -> currencyService.getExchangeRate("CAD", "AED"));
    }

    @Test
    void getAllCurrencies_shouldReturnAllSixCurrencies() {
        Currency inr = mock(Currency.class);
        when(inr.getCurrencyCode()).thenReturn("INR");
        when(inr.getCurrencyName()).thenReturn("Indian Rupees");
        when(inr.getCountryName()).thenReturn("INDIA");

        Currency usd = mock(Currency.class);
        when(usd.getCurrencyCode()).thenReturn("USD");
        when(usd.getCurrencyName()).thenReturn("US Dollars");
        when(usd.getCountryName()).thenReturn("USA");

        Currency cad = mock(Currency.class);
        when(cad.getCurrencyCode()).thenReturn("CAD");
        when(cad.getCurrencyName()).thenReturn("Canadian Dollars");
        when(cad.getCountryName()).thenReturn("CANADA");

        Currency eur = mock(Currency.class);
        when(eur.getCurrencyCode()).thenReturn("EUR");
        when(eur.getCurrencyName()).thenReturn("European Dollars");
        when(eur.getCountryName()).thenReturn("EUROPE");

        Currency aud = mock(Currency.class);
        when(aud.getCurrencyCode()).thenReturn("AUD");
        when(aud.getCurrencyName()).thenReturn("Australian Dollars");
        when(aud.getCountryName()).thenReturn("AUSTRALIA");

        Currency aed = mock(Currency.class);
        when(aed.getCurrencyCode()).thenReturn("AED");
        when(aed.getCurrencyName()).thenReturn("UAE Dirham");
        when(aed.getCountryName()).thenReturn("UAE");

        when(currencyRepository.findAll()).thenReturn(List.of(inr, usd, cad, eur, aud, aed));

        Map<String, CurrencyResponse> response = currencyService.getAllCurrencies();

        assertEquals(6, response.size());
        assertEquals("USD", response.get("USD").currencyCode());
    }
}
