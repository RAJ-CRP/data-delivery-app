package com.project.DataDelivery.Validations;

import java.io.File;

public class CommonValidations {
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty() && !str.trim().isEmpty();
    }

    public static boolean isValidLength(String str, Integer length) {
        return str.length() <= length;
    }

    public static boolean isValidDirectoryPath(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.exists() && directory.isDirectory();
    }

    public static boolean isValidFilePath(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
}
