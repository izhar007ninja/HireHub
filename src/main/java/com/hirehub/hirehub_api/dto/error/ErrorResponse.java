package com.hirehub.hirehub_api.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse (
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timeStamp
){

    public static ErrorResponse of(int status,String error,String message,String path){
        return new ErrorResponse(status,error,message,path,LocalDateTime.now());
    }
}
