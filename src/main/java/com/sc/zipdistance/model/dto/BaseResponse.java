package com.sc.zipdistance.model.dto;

import lombok.Data;

@Data
public class BaseResponse<T> {

    private String status;
    private String message;
    private T result;

    public BaseResponse() {
    }

    public BaseResponse(T data, String status, String message) {
        this.result = data;
        this.status = status;
        this.message = message;
    }
}
