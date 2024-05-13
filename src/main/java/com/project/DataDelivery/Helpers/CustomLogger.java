package com.project.DataDelivery.Helpers;

import com.project.DataDelivery.Exceptions.MyExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomLogger {
    private static final Logger logger = LogManager.getLogger(CustomLogger.class);
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void logSuccess(String message, Long startTime, Long endTime) {
        double executionTime = (double) (endTime - startTime) / 1_000_000_000;
        System.out.println(ANSI_GREEN + "\n[SUCCESS] " + message + " : TIME TAKEN " + executionTime + " seconds" + ANSI_RESET + "\n");
    }

    public static void logSuccess(String message) {
        System.out.println(ANSI_GREEN + "\n[SUCCESS] " + message + ANSI_RESET + "\n");
    }

    public static void logError(String message, Object error) {
        System.err.println(ANSI_RED + "\n[ERROR] " + message + ANSI_RESET + " : " + error + "\n");
    }

    public static void logError(String message) {
        System.err.println(ANSI_RED + "\n[ERROR] " + message + ANSI_RESET + "\n");
    }

    public static void logWarning(String message) {
        System.out.println(ANSI_YELLOW + "\n[WARNING] " + message + ANSI_RESET + "\n");
    }
}
