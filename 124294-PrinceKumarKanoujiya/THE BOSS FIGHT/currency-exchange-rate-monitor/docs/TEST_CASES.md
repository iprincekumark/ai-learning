# Test Cases

## Backend API and Service Test Cases

| Test Case ID | Scenario | Type | Expected Result |
|---|---|---|---|
| BE-01 | Fetch all currencies | Positive | Returns 6 currencies with code/name/country |
| BE-02 | USD → INR direct rate | Positive | HTTP 200, exchangeRate = 80.08 |
| BE-03 | INR → USD inverse rate | Positive | HTTP 200, exchangeRate = 0.0125 |
| BE-04 | USD → USD same currency | Positive | HTTP 200, exchangeRate = 1.0 |
| BE-05 | USD → CAD direct rate | Positive | HTTP 200, exchangeRate = 1.36 |
| BE-06 | usd → inr lowercase handling | Positive | HTTP 200, normalized response (USD/INR) |
| BE-07 | XXX → INR invalid from currency | Negative | HTTP 400, currency validation error |
| BE-08 | USD → XXX invalid to currency | Negative | HTTP 400, currency validation error |
| BE-09 | CAD → AED missing exchange rate | Negative | HTTP 404, rate not found error |

## Frontend Manual Test Cases

| Test Case ID | Scenario | Type | Expected Result |
|---|---|---|---|
| FE-01 | Currency list loads on page load | Positive | Both dropdowns show currencies |
| FE-02 | Currency 1 excluded from Currency 2 | Positive | Selected Currency 1 is removed from Currency 2 options |
| FE-03 | Submit without selections | Negative | Validation error shown to user |
| FE-04 | Valid submit | Positive | Exchange rate displayed in result card |
| FE-05 | API error response | Negative | User-friendly error message shown |
