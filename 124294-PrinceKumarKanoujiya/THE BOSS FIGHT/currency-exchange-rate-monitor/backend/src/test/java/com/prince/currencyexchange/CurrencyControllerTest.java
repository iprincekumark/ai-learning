package com.prince.currencyexchange;

import com.prince.currencyexchange.controller.CurrencyController;
import com.prince.currencyexchange.dto.CurrencyResponse;
import com.prince.currencyexchange.dto.ExchangeRateResponse;
import com.prince.currencyexchange.exception.CurrencyNotFoundException;
import com.prince.currencyexchange.exception.GlobalExceptionHandler;
import com.prince.currencyexchange.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        when(currencyService.getExchangeRate("USD", "INR")).thenReturn(new ExchangeRateResponse("USD", "INR", new BigDecimal("80.08")));
        mockMvc.perform(get("/exchange-rate/USD/INR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchangeRate").value(80.08));
    }

    @Test
    void invalidCurrencyReturnsBadRequest() throws Exception {
        when(currencyService.getExchangeRate("XXX", "INR")).thenThrow(new CurrencyNotFoundException("Unsupported currency code: XXX"));
        mockMvc.perform(get("/exchange-rate/XXX/INR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported currency code: XXX"));
    }
}
