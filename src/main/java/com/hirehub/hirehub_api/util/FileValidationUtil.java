package com.hirehub.hirehub_api.util;

import com.hirehub.hirehub_api.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidationUtil {

    private static final long MAX_FILE_SIZE = 5*1024*1024;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
      "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword"
    );

    public static void validateFile(MultipartFile file){
        if (file == null || file.isEmpty()){
            throw new BadRequestException("Uploaded file cannot be empty");
        }
        if (file.getSize()>MAX_FILE_SIZE){
            throw new BadRequestException("File cannot exceed the max limit");
        }
        String contentType = file.getContentType();
        if (contentType==null || !ALLOWED_CONTENT_TYPES.contains(contentType)){
            throw new BadRequestException("Only pdf, docs, doc allowed");
        }

    }
}
