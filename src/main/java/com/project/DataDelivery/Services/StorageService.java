package com.project.DataDelivery.Services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.project.DataDelivery.Exceptions.ProcessException;
import com.project.DataDelivery.Helpers.CustomLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
public class StorageService {
    @Value("${application.bucket.name}")
    private String S3_BUCKET_NAME;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(String fileName, String directoryPath) {
        long startTime = System.nanoTime();
        String localFilePath = directoryPath + File.separator + fileName;
        String uniqueFileName = getUniqueFileName(fileName);
        File file = new File(localFilePath);

        s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, uniqueFileName, file));
        file.delete();

        long endTime = System.nanoTime();
        CustomLogger.logSuccess("File uploaded to S3.", startTime, endTime);
        return uniqueFileName;
    }

    public void downloadFile(String fileName, String directoryPath) {
        long startTime = System.nanoTime();

        S3Object s3Object = s3Client.getObject(S3_BUCKET_NAME, fileName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

        String localFilePath = directoryPath + File.separator + fileName;

        try (FileOutputStream fileOutputStream = new FileOutputStream(localFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = s3ObjectInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException ioException) {
            CustomLogger.logError("Could not download the file from S3", ioException);
            throw new ProcessException("Could not download the file from S3");
        }

        long endTime = System.nanoTime();
        CustomLogger.logSuccess("File downloaded from S3.", startTime, endTime);
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(S3_BUCKET_NAME, fileName);
    }

    private String getUniqueFileName(String fileName) {
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        return fileNameWithoutExtension + "_" + System.currentTimeMillis() + extension;
    }
}
