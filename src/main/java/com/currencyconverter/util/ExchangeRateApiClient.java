package com.currencyconverter.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * ExchangeRateApiClient — thin HTTP wrapper around ExchangeRate-API v6.
 *
 * Responsibilities:
 *   • Build the request URL from {@link AppConstants}
 *   • Perform an HTTP GET with a sensible timeout
 *   • Parse the JSON response with GSON
 *   • Extract the specific conversion rate requested
 *
 * This class has NO dependency on JavaFX or JDBC — it belongs to the
 * infrastructure / utility layer and can be unit-tested in isolation.
 */
public final class ExchangeRateApiClient {

    /** Connection + read timeout in milliseconds. */
    private static final int TIMEOUT_MS = 8_000;

    // Private constructor — static utility class
    private ExchangeRateApiClient() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /**
     * Fetches the exchange rate from {@code fromCurrency} to {@code toCurrency}.
     *
     * @param fromCurrency ISO 4217 base currency code (e.g. "USD")
     * @param toCurrency   ISO 4217 target currency code (e.g. "EUR")
     * @return the rate as a {@link BigDecimal}, never {@code null}
     * @throws IOException      if the HTTP request fails or times out
     * @throws RuntimeException if the API returns an error or the rate is missing
     */
    public static BigDecimal fetchRate(String fromCurrency, String toCurrency)
            throws IOException {

        // 1. Build the URL
        String endpoint = String.format(AppConstants.API_BASE_URL,
                AppConstants.API_KEY, fromCurrency.toUpperCase());

        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");

            // 2. Check HTTP status
            int httpStatus = connection.getResponseCode();
            if (httpStatus != HttpURLConnection.HTTP_OK) {
                throw new IOException(
                        "API request failed — HTTP " + httpStatus + " for URL: " + endpoint);
            }

            // 3. Read and parse JSON
            try (InputStream is = connection.getInputStream();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                // Check API-level result field
                String result = root.has("result")
                        ? root.get("result").getAsString()
                        : "unknown";

                if (!"success".equalsIgnoreCase(result)) {
                    String errorType = root.has("error-type")
                            ? root.get("error-type").getAsString()
                            : "unknown error";
                    throw new RuntimeException(
                            "ExchangeRate-API returned error: " + errorType);
                }

                // Extract conversion_rates object
                JsonObject rates = root.getAsJsonObject("conversion_rates");
                if (rates == null) {
                    throw new RuntimeException(
                            "Malformed API response: 'conversion_rates' field missing");
                }

                // Look up target currency
                JsonElement rateElement = rates.get(toCurrency.toUpperCase());
                if (rateElement == null || rateElement.isJsonNull()) {
                    throw new RuntimeException(
                            "Rate not found for currency: " + toCurrency);
                }

                return rateElement.getAsBigDecimal();
            }

        } finally {
            connection.disconnect();
        }
    }
}
