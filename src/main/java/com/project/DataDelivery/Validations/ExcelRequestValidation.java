package com.project.DataDelivery.Validations;

import com.project.DataDelivery.Entity.ExcelRequest;
import com.project.DataDelivery.Exceptions.ValidationException;

import java.util.HashMap;
import java.util.Map;

public class ExcelRequestValidation {
    public static void validate(ExcelRequest excelRequest) {
        Map<String, String> errors = new HashMap<>();

        validateInputPath(errors, excelRequest.getInputPath());
        validateOutputPath(errors, excelRequest.getOutputPath());
        validateTableName(errors, excelRequest.getTableName());

        if (!errors.isEmpty()) {
            throw new ValidationException("Excel request validation failed...!", errors);
        }
    }

    public static void validateInputPath(Map<String, String> errors, String inputPath) {
        if (!CommonValidations.isNotEmpty(inputPath)) {
            errors.put("inputPath", "Input path cannot be empty or null.");
        } else if(!CommonValidations.isValidDirectoryPath(inputPath)) {
            errors.put("inputPath", "The provided input path does not exist or is not a valid directory.");
        }
    }

    public static void validateOutputPath(Map<String, String> errors, String outputPath) {
        if (!CommonValidations.isNotEmpty(outputPath)) {
            errors.put("outputPath", "Output path cannot be empty or null.");
        } else if (!CommonValidations.isValidDirectoryPath(outputPath)) {
            errors.put("outputPath", "The provided output path does not exist or is not a valid directory.");
        }
    }

    public static void validateTableName(Map<String, String> errors, String tableName) {
        if (!CommonValidations.isNotEmpty(tableName)) {
            errors.put("tableName", "Table name cannot be empty or null.");
        }
    }
}
