package com.currencyconverter.service;

import com.currencyconverter.dao.ConversionHistoryDao;
import com.currencyconverter.dao.ConversionHistoryDaoImpl;
import com.currencyconverter.model.ConversionRecord;
import com.currencyconverter.util.AppConstants;
import com.currencyconverter.util.ExchangeRateApiClient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * ConversionService — the application's business-logic layer.
 *
 * Responsibilities:
 *   1. Validate user input (delegate to {@link InputValidator})
 *   2. Fetch a live exchange rate via {@link ExchangeRateApiClient}
 *   3. Perform the arithmetic conversion
 *   4. Persist the result through the DAO
 *   5. Provide history retrieval for the UI
 *
 * The controller layer depends ONLY on this service — it never
 * touches the DAO or API client directly, keeping the layers clean.
 */
public class ConversionService {

    // ---------------------------------------------------------------
    // Dependencies (injected via constructor for testability)
    // ---------------------------------------------------------------

    private final ConversionHistoryDao historyDao;

    /** Precision context for multiplication — 10 significant figures. */
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /** Default constructor wires up the real JDBC DAO. */
    public ConversionService() {
        this(new ConversionHistoryDaoImpl());
    }

    /** Injection constructor — pass a mock DAO for unit tests. */
    public ConversionService(ConversionHistoryDao historyDao) {
        this.historyDao = historyDao;
    }

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /**
     * Validates input, fetches the live exchange rate, computes the
     * converted amount, persists the record, and returns it.
     *
     * @param fromCurrency source ISO currency code, e.g. "USD"
     * @param toCurrency   target ISO currency code, e.g. "EUR"
     * @param amountText   raw string from the UI input field
     * @return a fully-populated {@link ConversionRecord}
     * @throws IllegalArgumentException if validation fails
     * @throws Exception                if the API call or DB save fails
     */
    public ConversionRecord convert(String fromCurrency,
                                    String toCurrency,
                                    String amountText) throws Exception {

        // --- 1. Validate input ---
        InputValidator.validateCurrencyCode(fromCurrency, "From currency");
        InputValidator.validateCurrencyCode(toCurrency,   "To currency");
        InputValidator.validateSameCurrency(fromCurrency, toCurrency);

        BigDecimal amount = InputValidator.parseAndValidateAmount(amountText);

        // --- 2. Fetch live rate ---
        BigDecimal rate = ExchangeRateApiClient.fetchRate(fromCurrency, toCurrency);

        // --- 3. Compute converted amount ---
        BigDecimal result = amount.multiply(rate, MC)
                                  .setScale(6, RoundingMode.HALF_UP);

        // --- 4. Persist ---
        ConversionRecord record = ConversionRecord.forInsert(
                fromCurrency, toCurrency, amount, result, rate);

        historyDao.save(record);

        return record;
    }

    /**
     * Returns the last {@link AppConstants#HISTORY_LIMIT} conversions
     * from the database, newest first.
     *
     * @return list of recent records; may be empty, never {@code null}
     * @throws Exception if the database query fails
     */
    public List<ConversionRecord> getRecentHistory() throws Exception {
        return historyDao.findRecent(AppConstants.HISTORY_LIMIT);
    }

    /**
     * Fetches only the exchange rate without performing or saving a
     * conversion.  Useful for displaying a "current rate" preview.
     *
     * @param fromCurrency source ISO currency code
     * @param toCurrency   target ISO currency code
     * @return the current rate
     * @throws Exception if validation or the API call fails
     */
    public BigDecimal getRate(String fromCurrency, String toCurrency) throws Exception {
        InputValidator.validateCurrencyCode(fromCurrency, "From currency");
        InputValidator.validateCurrencyCode(toCurrency,   "To currency");
        return ExchangeRateApiClient.fetchRate(fromCurrency, toCurrency);
    }
}
