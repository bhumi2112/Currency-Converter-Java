package com.currencyconverter.service;

import java.math.BigDecimal;

/**
 * InputValidator — stateless helper that enforces all business rules
 * around user-supplied data before any API call or database write occurs.
 *
 * All methods throw {@link IllegalArgumentException} with a clear,
 * user-readable message so the controller can display it directly.
 */
final class InputValidator {

    /** Maximum amount the converter will process in one go. */
    private static final BigDecimal MAX_AMOUNT =
            new BigDecimal("999_999_999_999.999999".replace("_", ""));

    private InputValidator() { /* utility class */ }

    // ---------------------------------------------------------------
    // Validators
    // ---------------------------------------------------------------

    /**
     * Checks that a currency code is non-null and non-blank.
     *
     * @param code      the currency code to check
     * @param fieldName label used in the error message (e.g. "From currency")
     * @throws IllegalArgumentException if the code is null or blank
     */
    static void validateCurrencyCode(String code, String fieldName) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must be selected.");
        }
    }

    /**
     * Ensures the source and target currencies are not the same.
     *
     * @throws IllegalArgumentException if {@code from} equals {@code to}
     *                                   (case-insensitive)
     */
    static void validateSameCurrency(String from, String to) {
        if (from != null && from.equalsIgnoreCase(to)) {
            throw new IllegalArgumentException(
                    "From and To currencies must be different.");
        }
    }

    /**
     * Parses the raw amount string and validates it is:
     * <ul>
     *   <li>non-null / non-blank</li>
     *   <li>a valid number</li>
     *   <li>greater than zero</li>
     *   <li>within the allowed maximum</li>
     * </ul>
     *
     * @param amountText raw text from the UI input field
     * @return the parsed {@link BigDecimal}
     * @throws IllegalArgumentException if any rule is violated
     */
    static BigDecimal parseAndValidateAmount(String amountText) {
        if (amountText == null || amountText.isBlank()) {
            throw new IllegalArgumentException(
                    "Amount field must not be empty.");
        }

        // Strip commas that users sometimes type as thousands separators
        String cleaned = amountText.trim().replace(",", "");

        BigDecimal amount;
        try {
            amount = new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "\"" + amountText + "\" is not a valid number.  "
                    + "Please enter digits only (e.g. 1234.56).");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }

        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException(
                    "Amount exceeds the maximum allowed value ("
                    + MAX_AMOUNT.toPlainString() + ").");
        }

        return amount;
    }
}
