package com.currencyconverter.dao;

import com.currencyconverter.model.ConversionRecord;
import com.currencyconverter.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ConversionHistoryDaoImpl — JDBC-backed implementation of
 * {@link ConversionHistoryDao}.
 *
 * Each public method opens its own connection via {@link DatabaseUtil}
 * and closes it inside a try-with-resources block, keeping the code
 * simple and avoiding connection-leak bugs.
 *
 * SQL statements use {@link PreparedStatement} with positional
 * parameters to prevent SQL-injection attacks.
 */
public class ConversionHistoryDaoImpl implements ConversionHistoryDao {

    // ---------------------------------------------------------------
    // SQL statements
    // ---------------------------------------------------------------

    /** Insert one row into conversion_history. */
    private static final String SQL_INSERT = """
            INSERT INTO conversion_history
                (from_currency, to_currency, input_amount,
                 converted_result, exchange_rate)
            VALUES (?, ?, ?, ?, ?)
            """;

    /** Retrieve the N most recent rows, newest first. */
    private static final String SQL_FIND_RECENT = """
            SELECT id, from_currency, to_currency,
                   input_amount, converted_result,
                   exchange_rate, converted_at
            FROM   conversion_history
            ORDER  BY converted_at DESC
            LIMIT  ?
            """;

    // ---------------------------------------------------------------
    // Interface implementation
    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * Opens a connection, executes an INSERT, then closes the connection.
     */
    @Override
    public void save(ConversionRecord record) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, record.getFromCurrency());
            ps.setString(2, record.getToCurrency());
            ps.setBigDecimal(3, record.getInputAmount());
            ps.setBigDecimal(4, record.getConvertedResult());
            ps.setBigDecimal(5, record.getExchangeRate());

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("INSERT affected " + rows + " rows — expected 1");
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Opens a connection, executes a SELECT, maps each row to a
     * {@link ConversionRecord}, and returns the list.
     */
    @Override
    public List<ConversionRecord> findRecent(int limit) throws Exception {
        List<ConversionRecord> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_RECENT)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }

        return results;
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    /**
     * Maps the current row of the given {@link ResultSet} to a
     * {@link ConversionRecord}.  The ResultSet cursor must already
     * be positioned on a valid row when this is called.
     */
    private ConversionRecord mapRow(ResultSet rs) throws SQLException {
        long          id              = rs.getLong("id");
        String        fromCurrency    = rs.getString("from_currency");
        String        toCurrency      = rs.getString("to_currency");
        BigDecimal    inputAmount     = rs.getBigDecimal("input_amount");
        BigDecimal    convertedResult = rs.getBigDecimal("converted_result");
        BigDecimal    exchangeRate    = rs.getBigDecimal("exchange_rate");

        // Convert java.sql.Timestamp → LocalDateTime (UTC as stored)
        Timestamp ts = rs.getTimestamp("converted_at");
        LocalDateTime convertedAt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();

        return new ConversionRecord(
                id, fromCurrency, toCurrency,
                inputAmount, convertedResult, exchangeRate, convertedAt);
    }
}
