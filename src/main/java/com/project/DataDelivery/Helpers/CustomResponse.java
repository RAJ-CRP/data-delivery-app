package com.project.DataDelivery.Helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomResponse<T> {
    private String status;
    private String message;
    private T data;
    private T error;

    public static <T> CustomResponse<T> success(String message, T data) {
        return new CustomResponse<>("success", message, data, null);
    }

    public static <T> CustomResponse<T> success(String message) {
        return new CustomResponse<>("success", message, null, null);
    }

    public static <T> CustomResponse<T> error(String message, T error) {
        return new CustomResponse<>("error", message, null, error);
    }

    public static <T> CustomResponse<T> error(String message) {
        return new CustomResponse<>("error", message, null, null);
    }
}
