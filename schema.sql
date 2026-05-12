-- ============================================================
-- Currency Converter - MySQL Database Setup Script
-- Run this script in your MySQL client before launching the app
-- ============================================================

-- Create the database (if it doesn't already exist)
CREATE DATABASE IF NOT EXISTS currency_converter_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Switch to the new database
USE currency_converter_db;

-- ---------------------------------------------------------------
-- Table: conversion_history
-- Stores every conversion the user performs via the application
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS conversion_history (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    from_currency   VARCHAR(10)     NOT NULL COMMENT 'ISO 4217 currency code, e.g. USD',
    to_currency     VARCHAR(10)     NOT NULL COMMENT 'ISO 4217 currency code, e.g. EUR',
    input_amount    DECIMAL(20, 6)  NOT NULL COMMENT 'Original amount entered by the user',
    converted_result DECIMAL(20, 6) NOT NULL COMMENT 'Result after applying the exchange rate',
    exchange_rate   DECIMAL(20, 8)  NOT NULL COMMENT 'Rate used: 1 from_currency = X to_currency',
    converted_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC timestamp of the conversion',

    PRIMARY KEY (id),
    INDEX idx_converted_at (converted_at DESC)   -- speeds up "last 10 records" queries
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='Stores all currency conversion history';

-- ---------------------------------------------------------------
-- (Optional) Seed a few rows so "View History" has data right away
-- ---------------------------------------------------------------
-- INSERT INTO conversion_history
--     (from_currency, to_currency, input_amount, converted_result, exchange_rate)
-- VALUES
--     ('USD', 'EUR', 100.000000, 92.500000, 0.92500000),
--     ('EUR', 'GBP',  50.000000, 42.800000, 0.85600000),
--     ('GBP', 'INR', 200.000000, 21200.000000, 106.00000000);

-- ---------------------------------------------------------------
-- Confirm the table was created
-- ---------------------------------------------------------------
DESCRIBE conversion_history;
