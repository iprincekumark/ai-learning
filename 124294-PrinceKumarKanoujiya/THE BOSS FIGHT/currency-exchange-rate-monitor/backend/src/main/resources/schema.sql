CREATE TABLE currency (id BIGINT PRIMARY KEY,currency_code VARCHAR(10) NOT NULL UNIQUE,currency_name VARCHAR(100) NOT NULL,country_name VARCHAR(100) NOT NULL);
CREATE TABLE currency_rate (currency_code_from VARCHAR(10) NOT NULL,currency_code_to VARCHAR(10) NOT NULL,exchange_rate DECIMAL(10, 4) NOT NULL,PRIMARY KEY (currency_code_from, currency_code_to));
