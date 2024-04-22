package com.project.DataDelivery.Controller;

import com.project.DataDelivery.Helpers.CustomResponse;
import com.project.DataDelivery.Services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class StorageController {
    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam String fileName, @RequestParam String inputPath) {
        String uploadedFileName = storageService.uploadFile(fileName, inputPath);
        Map<String, String> responseData = new HashMap<>();
        responseData.put("uploadedFileName", uploadedFileName);

        return new ResponseEntity<>(CustomResponse.success("File Uploaded Successfully.", responseData), HttpStatus.OK);
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String fileName, @RequestParam String outputPath) {
        storageService.downloadFile(fileName, outputPath);
        return new ResponseEntity<>(CustomResponse.success("File Downloaded Successfully."), HttpStatus.OK);
    }

    @PostMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        storageService.deleteFile(fileName);
        return new ResponseEntity<>(CustomResponse.success("File deleted successfully."), HttpStatus.OK);
    }
}
