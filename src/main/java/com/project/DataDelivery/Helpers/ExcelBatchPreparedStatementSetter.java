package com.project.DataDelivery.Helpers;

import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExcelBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    private final Sheet sheet;
    private final int batchSize;

    public ExcelBatchPreparedStatementSetter(Sheet sheet, int batchSize) {
        this.sheet = sheet;
        this.batchSize = batchSize;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Row row = sheet.getRow(i + 1);
        if (row != null) {
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                ps.setString(j + 1, getCellValue(cell));
            }
        }
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    public String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                return String.format("%.0f", cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case STRING:
                return cell.getStringCellValue();
            default:
                return "";
        }
    }
}