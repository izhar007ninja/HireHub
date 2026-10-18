package com.hirehub.hirehub_api.service.storage;

import com.hirehub.hirehub_api.exception.BadRequestException;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService{

    private final Path fileStorageLocation;
    private final String baseUrl;

    public FileStorageServiceImpl(@Value("${hirehub.storage.upload-dir:uploads/resume}") String fileStorageLocation,@Value("${hirehub.storage.base-url:http://localhost:8080/api/resumes}") String baseUrl) {
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/")? baseUrl.substring(0,baseUrl.length()-1): baseUrl;
    }


    @PostConstruct
    private void init(){
        try{
            Files.createDirectories(this.fileStorageLocation);
        }
        catch (IOException e){
            throw new RuntimeException("could not create directory"+e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long userId) {

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if (originalFileName.contains("..")){
            throw new BadRequestException("File name contains invalid sequence");
        }

        String fileExtension = "";
        int extIndex = originalFileName.lastIndexOf(".");
        if(extIndex>0){
            fileExtension = originalFileName.substring(extIndex);
        }

        String generatedFileName = String.format("%d_%s%s",userId, UUID.randomUUID(),fileExtension);

        try{
            Path targetLocation = this.fileStorageLocation.resolve(generatedFileName);
            Files.copy(file.getInputStream(),targetLocation,StandardCopyOption.REPLACE_EXISTING);
            return String.format("%s/%s",this.baseUrl,generatedFileName);
        }
        catch (IOException ex){
            throw new RuntimeException("Could not store file " + generatedFileName + ". Please try again.", ex);
        }

    }

    @Override
    public Resource loadFileAsresource(String fileName) {
        try{
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (!filePath.startsWith(this.fileStorageLocation)){
                throw new BadRequestException("Access denied file outsize the current directory");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()){
                return resource;
            }
            else{
                throw new ResourceNotFoundException("resource was not found");
            }

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException( "File path invalid"+e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try{
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.delete(filePath);
        }
        catch (IOException e){
            throw new ResourceNotFoundException("Could not delete the file"+e);
        }

    }
}
