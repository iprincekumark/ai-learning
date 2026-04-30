package com.prince.currencyexchange;

import com.prince.currencyexchange.controller.CurrencyController;
import com.prince.currencyexchange.dto.CurrencyResponse;
import com.prince.currencyexchange.dto.ExchangeRateResponse;
import com.prince.currencyexchange.exception.CurrencyNotFoundException;
import com.prince.currencyexchange.exception.ExchangeRateNotFoundException;
import com.prince.currencyexchange.exception.GlobalExceptionHandler;
import com.prince.currencyexchange.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CurrencyControllerTest {

    private CurrencyService currencyService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        currencyService = mock(CurrencyService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CurrencyController(currencyService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCurrencyReturnsAllSix() throws Exception {
        Map<String, CurrencyResponse> m = new LinkedHashMap<>();
        m.put("INR", new CurrencyResponse("INDIA", "INR", "Indian Rupees"));
        m.put("USD", new CurrencyResponse("USA", "USD", "US Dollars"));
        m.put("CAD", new CurrencyResponse("CANADA", "CAD", "Canadian Dollars"));
        m.put("EUR", new CurrencyResponse("EUROPE", "EUR", "European Dollars"));
        m.put("AUD", new CurrencyResponse("AUSTRALIA", "AUD", "Australian Dollars"));
        m.put("AED", new CurrencyResponse("UAE", "AED", "UAE Dirham"));
        when(currencyService.getAllCurrencies()).thenReturn(m);

        mockMvc.perform(get("/currency"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.USD.currencyCode").value("USD"));
    }

    @Test
    void usdInrReturnsDirect() throws Exception {
        when(currencyService.getExchangeRate("USD", "INR"))
                .thenReturn(new ExchangeRateResponse("USD", "INR", new BigDecimal("80.08")));

        mockMvc.perform(get("/exchange-rate/USD/INR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchangeRate").value(80.08));
    }

    @Test
    void inrUsdReturnsInverseRate() throws Exception {
        when(currencyService.getExchangeRate("INR", "USD"))
                .thenReturn(new ExchangeRateResponse("INR", "USD", new BigDecimal("0.0125")));

        mockMvc.perform(get("/exchange-rate/INR/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchangeRate").value(0.0125));
    }

    @Test
    void usdUsdReturnsSameCurrencyOne() throws Exception {
        when(currencyService.getExchangeRate("USD", "USD"))
                .thenReturn(new ExchangeRateResponse("USD", "USD", new BigDecimal("1.0")));

        mockMvc.perform(get("/exchange-rate/USD/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchangeRate").value(1.0));
    }

    @Test
    void usdCadReturnsDirectRate() throws Exception {
        when(currencyService.getExchangeRate("USD", "CAD"))
                .thenReturn(new ExchangeRateResponse("USD", "CAD", new BigDecimal("1.36")));

        mockMvc.perform(get("/exchange-rate/USD/CAD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchangeRate").value(1.36));
    }

    @Test
    void lowercaseInputReturnsSuccess() throws Exception {
        when(currencyService.getExchangeRate("usd", "inr"))
                .thenReturn(new ExchangeRateResponse("USD", "INR", new BigDecimal("80.08")));

        mockMvc.perform(get("/exchange-rate/usd/inr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrencyCode").value("USD"));
    }

    @Test
    void invalidFromCurrencyReturnsBadRequest() throws Exception {
        when(currencyService.getExchangeRate("XXX", "INR"))
                .thenThrow(new CurrencyNotFoundException("Unsupported currency code: XXX"));

        mockMvc.perform(get("/exchange-rate/XXX/INR"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidToCurrencyReturnsBadRequest() throws Exception {
        when(currencyService.getExchangeRate("USD", "XXX"))
                .thenThrow(new CurrencyNotFoundException("Unsupported currency code: XXX"));

        mockMvc.perform(get("/exchange-rate/USD/XXX"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingRateReturnsNotFound() throws Exception {
        when(currencyService.getExchangeRate("CAD", "AED"))
                .thenThrow(new ExchangeRateNotFoundException("Exchange rate not found for CAD to AED"));

        mockMvc.perform(get("/exchange-rate/CAD/AED"))
                .andExpect(status().isNotFound());
    }
}
