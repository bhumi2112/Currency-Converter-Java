package com.currencyconverter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseUtil — centralised JDBC connection factory.
 *
 * All database credentials live here so they only need to be
 * changed in one place.  Replace the placeholder values with
 * your actual MySQL server details before running the application.
 *
 * Usage:
 *   try (Connection conn = DatabaseUtil.getConnection()) {
 *       // use conn ...
 *   }
 *
 * The try-with-resources block returns the connection to the pool
 * (or closes it) automatically — no manual close() needed.
 */
public final class DatabaseUtil {

    // ---------------------------------------------------------------
    // ⚙️  Configuration — edit these four constants
    // ---------------------------------------------------------------

    /** Hostname or IP address of your MySQL server. */
    private static final String HOST     = "localhost";

    /** MySQL port (default 3306). */
    private static final int    PORT     = 3306;

    /** Database / schema name (must exist — see schema.sql). */
    private static final String DATABASE = "currency_converter_db";

    /** MySQL user that has SELECT / INSERT rights on the database. */
    private static final String USER     = "User_name";

    /**
     * Password for the MySQL user above.
     * Replace {@code "----"} with your actual password.
     */
    private static final String PASSWORD = "******";

    // ---------------------------------------------------------------
    // Internal — derived JDBC URL
    // ---------------------------------------------------------------

    private static final String JDBC_URL = String.format(
            "jdbc:mysql://%s:%d/%s"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC"
                    + "&characterEncoding=UTF-8",
            HOST, PORT, DATABASE);

    // ---------------------------------------------------------------
    // Private constructor — utility class, not instantiable
    // ---------------------------------------------------------------

    private DatabaseUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /**
     * Opens and returns a new JDBC {@link Connection}.
     *
     * <p>The caller is responsible for closing the connection (preferably
     * via try-with-resources).
     *
     * @return an open {@link Connection} to the configured MySQL database
     * @throws SQLException if the driver is not found or the connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * Quick connectivity test — returns {@code true} when the database
     * can be reached, {@code false} otherwise.  Useful for startup checks.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DatabaseUtil] Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
