package com.project.DataDelivery.Controller;

import com.project.DataDelivery.Helpers.CustomLogger;
import com.project.DataDelivery.Helpers.CustomResponse;
import com.project.DataDelivery.Dto.ExcelRequest;
import com.project.DataDelivery.Services.ExcelService;
import com.project.DataDelivery.Services.StorageService;
import com.project.DataDelivery.Validations.ExcelRequestValidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExcelController {
    @Autowired
    private ExcelService excelService;

    @Autowired
    private StorageService storageService;

    @PostMapping("/process")
    public ResponseEntity<?> processRequest(@RequestBody ExcelRequest excelRequest) {
        long startTime = System.nanoTime();

        // Validate Excel Request
        ExcelRequestValidation.validate(excelRequest);

        // Import data from Excel file and Store it in a dynamically created table
        excelService.saveAllExcelFiles(excelRequest.getInputPath(), excelRequest.getTableName());

        // Export data to CSV file and Store it locally
        String savedFileName = excelService.exportDataToCsvMultiThreading(excelRequest.getOutputPath(), excelRequest.getTableName());

        // Upload CSV file to AWS S3
        String uploadedFileName = storageService.uploadFile(savedFileName, excelRequest.getOutputPath());

        // Download CSV file from AWS S3
        storageService.downloadFile(uploadedFileName, excelRequest.getOutputPath());


        long endTime = System.nanoTime();
        CustomLogger.logSuccess("PROCESS COMPLETED", startTime, endTime);

        return new ResponseEntity<>(CustomResponse.success("Excel Processing Completed."), HttpStatus.OK);
    }
}
