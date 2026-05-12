package com.currencyconverter.util;

import java.util.List;

/**
 * AppConstants — application-wide constants.
 *
 * ▶ Replace {@code YOUR_API_KEY_HERE} with your free key from
 *   https://www.exchangerate-api.com  (sign-up takes ~30 seconds).
 *
 * The free tier provides:
 *   • 1 500 requests / month
 *   • Hourly rate updates
 *   • 161 currencies
 */
public final class AppConstants {

    // ---------------------------------------------------------------
    // ⚙️  ExchangeRate-API configuration
    // ---------------------------------------------------------------

    /**
     * Your personal API key.
     * Replace {@code "YOUR_API_KEY_HERE"} before running the app.
     */
    public static final String API_KEY =
            "67b396d395da01592e86943e";

    /**
     * Base URL template.  {KEY} and {BASE} are replaced at runtime
     * by {@code ExchangeRateApiClient}.
     *
     * Full example:
     *   https://v6.exchangerate-api.com/v6/abc123/latest/USD
     */
    public static final String API_BASE_URL =
            "https://v6.exchangerate-api.com/v6/%s/latest/%s";

    // ---------------------------------------------------------------
    // Supported currencies shown in the ComboBoxes
    // ---------------------------------------------------------------

    /**
     * ISO 4217 currency codes that populate the From / To dropdowns.
     * Add or remove codes to suit your requirements.
     */
    public static final List<String> SUPPORTED_CURRENCIES = List.of(
            "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN",
            "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL",
            "BSD", "BTN", "BWP", "BYN", "BZD",
            "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC", "CUP", "CVE", "CZK",
            "DJF", "DKK", "DOP", "DZD",
            "EGP", "ERN", "ETB", "EUR",
            "FJD", "FKP",
            "GBP", "GEL", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD",
            "HKD", "HNL", "HRK", "HTG", "HUF",
            "IDR", "ILS", "INR", "IQD", "IRR", "ISK",
            "JMD", "JOD", "JPY",
            "KES", "KGS", "KHR", "KMF", "KRW", "KWD", "KYD", "KZT",
            "LAK", "LBP", "LKR", "LRD", "LSL", "LYD",
            "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRU", "MUR", "MVR",
            "MWK", "MXN", "MYR", "MZN",
            "NAD", "NGN", "NIO", "NOK", "NPR", "NZD",
            "OMR",
            "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG",
            "QAR",
            "RON", "RSD", "RUB", "RWF",
            "SAR", "SBD", "SCR", "SDG", "SEK", "SGD", "SHP", "SLL", "SOS", "SRD",
            "STN", "SVC", "SYP", "SZL",
            "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", "TWD", "TZS",
            "UAH", "UGX", "USD", "UYU", "UZS",
            "VES", "VND", "VUV",
            "WST",
            "XAF", "XCD", "XOF", "XPF",
            "YER",
            "ZAR", "ZMW", "ZWL"
    );

    /** Maximum number of history records shown in the UI table. */
    public static final int HISTORY_LIMIT = 10;

    // ---------------------------------------------------------------
    // Private constructor — utility class
    // ---------------------------------------------------------------

    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
