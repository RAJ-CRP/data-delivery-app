package com.project.DataDelivery.Helpers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsvRowMapper implements RowMapper<List<String>> {
    @Override
    public List<String> mapRow(ResultSet rs, int rowNum) throws SQLException {
        int columnCount = rs.getMetaData().getColumnCount();
        List<String> rowData = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            rowData.add(getStringOrNull(rs.getObject(i)));
        }
        return rowData;
    }

    private String getStringOrNull(Object obj) {
        return obj != null ? obj.toString() : null;
    }
}
