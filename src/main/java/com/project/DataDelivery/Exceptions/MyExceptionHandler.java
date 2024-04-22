package com.project.DataDelivery.Exceptions;

import com.project.DataDelivery.Helpers.CustomResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
//import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.Map;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LogManager.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(ProcessException.class)
    public ResponseEntity<CustomResponse<?>> handleProcessException(ProcessException e) {
        String responseMessage = "An error occurred while processing data...!";
        String errorMessage = e.getError();

        logger.error(errorMessage);
        return new ResponseEntity<>(CustomResponse.error(responseMessage, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomResponse<?>> handleValidationException(ValidationException e) {
        String responseMessage = e.getMessage();
        Map<String, String> errorMessage = e.getErrors();

        logger.error(responseMessage + ":" + errorMessage);
        return new ResponseEntity<>(CustomResponse.error(responseMessage, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ResponseEntity<Object> httpResponse = super.handleHttpMessageNotReadable(ex, headers, status, request);
        String responseMessage = "Invalid request...!";
        String errorMessage = ex.getMessage();

        logger.error(responseMessage + ":" + errorMessage);
        return new ResponseEntity<>(CustomResponse.error(responseMessage, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRunTimeException(RuntimeException e) {
        String responseMessage = "Oops..!";
        String errorMessage = e.getMessage();

        logger.error(responseMessage + ":" + errorMessage);
        return new ResponseEntity<>(CustomResponse.error(responseMessage, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<CustomResponse<?>> handleSQLException(SQLException e) {
        String responseMessage = "Invalid request...!";
        String errorMessage = e.getMessage().substring(e.getMessage().indexOf(')') + 1).trim();

        logger.error(responseMessage + ":" + e.getMessage());
        return new ResponseEntity<>(CustomResponse.error(responseMessage, errorMessage), HttpStatus.BAD_REQUEST);
    }
}
