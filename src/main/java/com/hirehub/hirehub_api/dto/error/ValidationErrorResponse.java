package com.hirehub.hirehub_api.dto.error;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Map<String,String> fieldErrors,
        LocalDateTime timeStamp
) {

    public static ValidationErrorResponse of(int status,String error,String message,String path,
                                             Map<String,String> fieldErrors
                                             ){
        return new ValidationErrorResponse(status,error,message,path,fieldErrors,LocalDateTime.now());
    }
}
