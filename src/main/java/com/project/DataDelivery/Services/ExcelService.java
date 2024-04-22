package com.project.DataDelivery.Services;

import com.project.DataDelivery.Exceptions.ProcessException;
import com.project.DataDelivery.Helpers.CustomLogger;
import com.project.DataDelivery.Repository.ExcelRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ExcelService {
    @Autowired
    private ExcelRepository excelRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final Logger logger = LogManager.getLogger(ExcelService.class);
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int BATCH_SIZE = 10_000;

    public void saveAllExcelFiles(String directoryPath, String tableName) {
        long startTime = System.nanoTime();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            CustomLogger.logError("No .xlsx files found in directory", directoryPath);
            throw new ProcessException("No .xlsx files found in directory: " + directoryPath);
        }

        // Saving Excel files into database using multithreading
        try {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            for (File file : files) {
                executor.execute(() -> saveExcelFile(file, tableName));
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            CustomLogger.logError("Thread execution interrupted", e);
            throw new ProcessException("Thread execution interrupted.");
        }

        long endTime = System.nanoTime();
        CustomLogger.logSuccess("All excel files inserted.", startTime, endTime);
    }

    public void saveExcelFile(File file, String tableName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet sheet = workbook.getSheetAt(0);
            excelRepository.saveExcelSheet(tableName, sheet);

            workbook.close();
            fileInputStream.close();
        } catch (IOException e) {
            CustomLogger.logError("Cannot read the excel file", e);
            throw new ProcessException("IO ERROR");
        }
    }

    public String exportDataToCsv(String outputPath, String tableName) {
        long startTime = System.nanoTime();

        String fileName = tableName + ".csv";
        String filePath = outputPath + File.separator + fileName;

        // Saving data as CSV file at temp location
        try {
            FileWriter writer = new FileWriter(filePath);
            List<String> columnNames = excelRepository.getColumnNames(tableName);
            writer.append(String.join(",", columnNames)).append("\n");

            long offset = 0;
            List<List<String>> rows;

            do {
                rows = excelRepository.getDataInBatch(tableName, BATCH_SIZE, offset);
                for (List<String> columns : rows) {
                    writer.append(String.join(",", columns)).append("\n");
                }
                offset += BATCH_SIZE;
            } while (rows.size() == BATCH_SIZE);

            writer.close();
        } catch (IOException e) {
            CustomLogger.logError("Could not complete the CSV generation", e);
            throw new ProcessException("IO ERROR");
        }

        long endTime = System.nanoTime();
        CustomLogger.logSuccess("CSV file generated.", startTime, endTime);

        return fileName;
    }

    public String exportDataToCsvMultiThreading(String outputPath, String tableName) {
        long startTime = System.nanoTime();

        String fileName = tableName + ".csv";
        String filePath = outputPath + File.separator + fileName;

        // Saving data as CSV file at temp location
        try {
            FileWriter writer = new FileWriter(filePath);
            List<String> columnNames = excelRepository.getColumnNames(tableName);

            writer.append(String.join(",", columnNames)).append("\n");

            long totalRows = excelRepository.getTotalRowsCount(tableName);

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            for (long i = 0; i < totalRows; i += BATCH_SIZE) {
                long startOffset = i;
                executor.execute(() -> {
                    try {
                        List<List<String>> rows = excelRepository.getDataInBatch(tableName, BATCH_SIZE, startOffset);
                        synchronized (writer) {
                            for (List<String> columns : rows) {
                                writer.append(String.join(",", columns)).append("\n");
                            }
                        }
                    } catch (IOException e) {
                        logger.error("IO ERROR", e);
                        throw new ProcessException("IO ERROR");
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            writer.close();
        } catch (IOException e) {
            logger.error("IO ERROR", e);
            throw new ProcessException("IO ERROR");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread execution interrupted: ", e);
            throw new ProcessException("Thread execution interrupted.");
        }

        long endTime = System.nanoTime();
        CustomLogger.logSuccess("CSV file generated.", startTime, endTime);

        return fileName;
    }

// Cursor Pagination
//            String mainIdName = "CHARGE_ID", secondIdName = "COMPANY_ID";
//            long mainIdValue = 0, secondIdValue = 0;
//            int mainIdIndex = columnNames.indexOf(mainIdName), secondIdIndex = columnNames.indexOf(secondIdName);
//            excelRepository.makeIndex(tableName, mainIdName, secondIdName);
// ------------------
//                batchData = excelRepository.getDataInBatch(tableName, BATCH_SIZE, mainIdName, mainIdValue, secondIdName, secondIdValue);
//                if (!batchData.isEmpty()) {
//                    List<String> lastRow = batchData.get(batchData.size() - 1);
//                    mainIdValue = Long.parseLong(lastRow.get(mainIdIndex));
//                    secondIdValue = Long.parseLong(lastRow.get(secondIdIndex));
//                }

}
