package com.hirehub.hirehub_api.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file,Long userId);
    Resource loadFileAsresource(String fileName);
    void deleteFile(String fileName);
}
