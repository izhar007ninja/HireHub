package com.hirehub.hirehub_api.controller;


import com.hirehub.hirehub_api.dto.profile.UploadResumeResponse;
import com.hirehub.hirehub_api.entity.CandidateProfile;
import com.hirehub.hirehub_api.entity.User;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import com.hirehub.hirehub_api.repository.CandidateProfileRepository;
import com.hirehub.hirehub_api.repository.UserRepository;
import com.hirehub.hirehub_api.service.storage.FileStorageService;
import com.hirehub.hirehub_api.util.FileValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@Tag(name = "Resume Storage",description = "You can upload resume or download resume")
public class ResumeController {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    public ResumeController(FileStorageService fileStorageService, UserRepository userRepository, CandidateProfileRepository candidateProfileRepository) {

        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
    }


    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Upload candidate resume",description = "Formats allowed - PDF, DOCS, DOC")
    @Transactional
    public ResponseEntity<UploadResumeResponse> uploadResume(@RequestParam("file")MultipartFile file, @AuthenticationPrincipal UserDetails userDetails){
        FileValidationUtil.validateFile(file);
        User candidate = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        String fileDownloadUrl = fileStorageService.storeFile(file,candidate.getId());
        String generatedFileName = fileDownloadUrl.substring(fileDownloadUrl.lastIndexOf("/")+1);

        CandidateProfile candidateProfile = candidateProfileRepository.findByUserId(candidate.getId())
                .orElseThrow(()->new ResourceNotFoundException("Candidate was not found"));
        candidateProfile.setResumeUrl(fileDownloadUrl);
        candidateProfileRepository.save(candidateProfile);
        return ResponseEntity.ok(new UploadResumeResponse(generatedFileName,fileDownloadUrl,file.getContentType(),file.getSize()));

    }

    @GetMapping("/{fileName}")
    @Operation(summary = "Download or view resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable String fileName, HttpServletRequest request){
        Resource resume = fileStorageService.loadFileAsresource(fileName);

        String contentType = null;
        try{
            contentType = request.getServletContext().getMimeType(resume.getFile().getAbsolutePath());
        }
        catch (Exception e){
        }
        if (contentType==null){
            contentType = "application/actet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resume.getFilename() + "\"")
                .body(resume);
    }


}
