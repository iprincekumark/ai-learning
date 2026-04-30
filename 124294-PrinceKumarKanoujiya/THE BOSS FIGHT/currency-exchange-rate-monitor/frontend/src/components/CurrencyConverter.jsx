import { useEffect, useMemo, useState } from "react";
import { fetchCurrencies, fetchRate } from "../api/currencyApi";

export default function CurrencyConverter() {
  const [currencies, setCurrencies] = useState({});
  const [fromCurrency, setFromCurrency] = useState("");
  const [toCurrency, setToCurrency] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchCurrencies()
      .then(setCurrencies)
      .catch((err) => setError(err.message));
  }, []);

  const codes = Object.keys(currencies);

  const toCurrencyOptions = useMemo(
    () => codes.filter((code) => code !== fromCurrency),
    [codes, fromCurrency]
  );

  useEffect(() => {
    if (fromCurrency && toCurrency === fromCurrency) {
      setToCurrency("");
    }
  }, [fromCurrency, toCurrency]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setResult(null);

    if (!fromCurrency || !toCurrency) {
      setError("Select both currencies");
      return;
    }

    try {
      const response = await fetchRate(fromCurrency, toCurrency);
      setResult(response);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="container">
      <h1>Currency Exchange Rate Monitor</h1>

      <form onSubmit={handleSubmit}>
        <select value={fromCurrency} onChange={(e) => setFromCurrency(e.target.value)}>
          <option value="">Currency 1</option>
          {codes.map((code) => (
            <option key={code} value={code}>
              {code}
            </option>
          ))}
        </select>

        <select value={toCurrency} onChange={(e) => setToCurrency(e.target.value)}>
          <option value="">Currency 2</option>
          {toCurrencyOptions.map((code) => (
            <option key={code} value={code}>
              {code}
            </option>
          ))}
        </select>

        <button type="submit">Get Exchange Rate</button>
      </form>

      {error && <p className="error">{error}</p>}

      {result && (
        <div className="card">
          1 {result.fromCurrencyCode} = {result.exchangeRate} {result.toCurrencyCode}
        </div>
      )}
    </div>
  );
}
