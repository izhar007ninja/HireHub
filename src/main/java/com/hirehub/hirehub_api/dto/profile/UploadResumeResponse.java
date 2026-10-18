package com.hirehub.hirehub_api.dto.profile;

public record UploadResumeResponse(
        String fileName,
        String fileUrl,
        String fileContentType,
        long fileSize
) {
}
