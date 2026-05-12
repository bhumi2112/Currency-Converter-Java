package com.currencyconverter.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ConversionRecord — immutable value object that represents one row
 * in the {@code conversion_history} MySQL table.
 *
 * Deliberately kept plain (no framework annotations) so it can be
 * used across all layers without coupling to persistence or UI code.
 */
public class ConversionRecord {

    // ---------------------------------------------------------------
    // Fields  (all final — instances are immutable after construction)
    // ---------------------------------------------------------------

    private final long          id;
    private final String        fromCurrency;
    private final String        toCurrency;
    private final BigDecimal    inputAmount;
    private final BigDecimal    convertedResult;
    private final BigDecimal    exchangeRate;
    private final LocalDateTime convertedAt;

    /** Shared formatter used for display; thread-safe in Java 8+. */
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public ConversionRecord(long          id,
                            String        fromCurrency,
                            String        toCurrency,
                            BigDecimal    inputAmount,
                            BigDecimal    convertedResult,
                            BigDecimal    exchangeRate,
                            LocalDateTime convertedAt) {
        this.id              = id;
        this.fromCurrency    = fromCurrency;
        this.toCurrency      = toCurrency;
        this.inputAmount     = inputAmount;
        this.convertedResult = convertedResult;
        this.exchangeRate    = exchangeRate;
        this.convertedAt     = convertedAt;
    }

    // ---------------------------------------------------------------
    // Convenience factory — used when saving a NEW record (id not yet known)
    // ---------------------------------------------------------------

    public static ConversionRecord forInsert(String     fromCurrency,
                                             String     toCurrency,
                                             BigDecimal inputAmount,
                                             BigDecimal convertedResult,
                                             BigDecimal exchangeRate) {
        return new ConversionRecord(0, fromCurrency, toCurrency,
                inputAmount, convertedResult, exchangeRate,
                LocalDateTime.now());
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public long          getId()              { return id;              }
    public String        getFromCurrency()    { return fromCurrency;    }
    public String        getToCurrency()      { return toCurrency;      }
    public BigDecimal    getInputAmount()     { return inputAmount;     }
    public BigDecimal    getConvertedResult() { return convertedResult; }
    public BigDecimal    getExchangeRate()    { return exchangeRate;    }
    public LocalDateTime getConvertedAt()     { return convertedAt;     }

    /** Human-readable timestamp for the UI table column. */
    public String getFormattedTimestamp() {
        return convertedAt != null ? convertedAt.format(DISPLAY_FMT) : "";
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return String.format("ConversionRecord{id=%d, %s→%s, %.4f→%.4f @ %.8f, at=%s}",
                id, fromCurrency, toCurrency,
                inputAmount, convertedResult, exchangeRate,
                getFormattedTimestamp());
    }
}
