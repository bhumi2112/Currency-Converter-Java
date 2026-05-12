package com.currencyconverter.dao;

import com.currencyconverter.model.ConversionRecord;

import java.util.List;

/**
 * ConversionHistoryDao — Data Access Object contract for the
 * {@code conversion_history} table.
 *
 * Programming to an interface lets us swap implementations
 * (e.g., for unit tests with an in-memory fake) without touching
 * the service or controller layers.
 */
public interface ConversionHistoryDao {

    /**
     * Persists a new conversion record in the database.
     *
     * @param record the record to insert; {@code id} and
     *               {@code convertedAt} are assigned by the database
     * @throws java.sql.SQLException if the insert fails
     */
    void save(ConversionRecord record) throws Exception;

    /**
     * Returns the most recent {@code limit} conversion records,
     * newest first.
     *
     * @param limit maximum number of records to return
     * @return ordered list of records (may be empty, never {@code null})
     * @throws java.sql.SQLException if the query fails
     */
    List<ConversionRecord> findRecent(int limit) throws Exception;
}
