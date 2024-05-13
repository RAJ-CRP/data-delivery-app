package com.project.DataDelivery.Repository;

import com.project.DataDelivery.Helpers.CsvRowMapper;
import com.project.DataDelivery.Helpers.CustomLogger;
import com.project.DataDelivery.Helpers.ExcelBatchPreparedStatementSetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.repeat;

@Repository
public class ExcelRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final CsvRowMapper csvRowMapper = new CsvRowMapper();
    private static final Logger logger = LogManager.getLogger(ExcelRepository.class);

    public void saveExcelSheet(String tableName, Sheet sheet) {
        try {
            long startTime = System.nanoTime();

            int totalRow = sheet.getLastRowNum();
            int totalColumn = sheet.getRow(0).getLastCellNum();
            String INSERT_DATA_QUERY = "INSERT INTO " + tableName + " VALUES (" + repeat("?, ", totalColumn - 1) + "?)";

            jdbcTemplate.batchUpdate(INSERT_DATA_QUERY, new ExcelBatchPreparedStatementSetter(sheet, totalRow));

            long endTime = System.nanoTime();
            logger.info("Excel file insert time : " + (double) (endTime - startTime) / 1_000_000_000 + " seconds");
        } catch (RuntimeException e) {
            logger.error("SAVE EXCEL ERROR: ", e);
        }
    }

    // OFFSET PAGINATION
    public List<List<String>> getDataInBatch(String tableName, long batchSize, long offset) {
        long startTime = System.nanoTime();

        String GET_DATA_IN_BATCH_QUERY = "SELECT * FROM " + tableName + " LIMIT ? OFFSET ?";
        List<List<String>> data = jdbcTemplate.query(GET_DATA_IN_BATCH_QUERY, new Object[]{batchSize, offset}, csvRowMapper);

        long endTime = System.nanoTime();
        long endOffset = offset + batchSize;
        logger.info("Batch [" + offset + " - " + endOffset + "] data read time : " + (double) (endTime - startTime) / 1_000_000_000 + " seconds");
        return data;
    }

    public long getTotalRowsCount(String tableName) {
        long startTime = System.nanoTime();

        String GET_ROW_COUNT_QUERY = "SELECT count(*) FROM " + tableName;
        long rowsCount = jdbcTemplate.queryForObject(GET_ROW_COUNT_QUERY, Long.class);

        long endTime = System.nanoTime();
        logger.info("Total rows count read time : " + (double) (endTime - startTime) / 1_000_000_000 + " seconds");
        return rowsCount;
    }

    // CURSOR PAGINATION
    /*
    public void makeIndex(String tableName, String mainIdName, String secondIdName) {
        long startTime = System.nanoTime();

        String convertIdsToINTQuery = "ALTER TABLE " + tableName + " MODIFY COLUMN " + mainIdName + " INT, MODIFY COLUMN " + secondIdName + " INT";
        String indexName = "idx_" + tableName + "_" + mainIdName + "_" + secondIdName;
        String createIndexQuery = "CREATE INDEX " + indexName + " ON " + tableName + " (" + mainIdName + ", " + secondIdName + ")";

        // Convert ID columns to INT type
        jdbcTemplate.update(convertIdsToINTQuery);
        System.out.println("Columns converted to INT type successfully.");

        // Create Index on table
        jdbcTemplate.update(createIndexQuery);
        System.out.println("Index created successfully: " + indexName);

        long endTime = System.nanoTime();
        System.out.println("Index Creation time (seconds): " + (double) (endTime - startTime) / 1_000_000_000 + " seconds");
    }


    public List<List<String>> getDataInBatch(String tableName, long batchSize, String mainIdName, long mainIdValue, String secondIdName, long secondIdValue) {
        long startTime = System.nanoTime();

        String GET_DATA_IN_BATCH_QUERY = "SELECT * FROM " + tableName + " WHERE (" + mainIdName + " > ? OR (" + mainIdName + " = ? AND " + secondIdName + " > ?)) ORDER BY " + mainIdName + " ASC, " + secondIdName + " ASC LIMIT ?";
        List<List<String>> data = jdbcTemplate.query(GET_DATA_IN_BATCH_QUERY, new Object[]{mainIdValue, mainIdValue, secondIdValue, batchSize}, csvRowMapper);

        long endTime = System.nanoTime();
        System.out.println("Batch data retrieved time (seconds): " + (double) (endTime - startTime) / 1_000_000_000 + " seconds");
        return data;
    }
*/

    public List<String> getColumnNames(String tableName) {
        String GET_COLUMN_NAMES_QUERY = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";
        return jdbcTemplate.queryForList(GET_COLUMN_NAMES_QUERY, String.class, tableName);
    }
}
