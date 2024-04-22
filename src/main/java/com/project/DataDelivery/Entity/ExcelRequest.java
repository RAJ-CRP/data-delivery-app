package com.project.DataDelivery.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRequest {
    private String inputPath;
    private String outputPath;
    private String tableName;
}
