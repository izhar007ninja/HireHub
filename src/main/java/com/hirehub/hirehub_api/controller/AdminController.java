package com.hirehub.hirehub_api.controller;

import com.hirehub.hirehub_api.dto.admin.AdminUserSummaryResponse;
import com.hirehub.hirehub_api.dto.admin.PlatformStatusResponse;
import com.hirehub.hirehub_api.dto.common.PageResponse;
import com.hirehub.hirehub_api.dto.job.JobResponse;
import com.hirehub.hirehub_api.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }


    @GetMapping("/status")
    public ResponseEntity<PlatformStatusResponse> getStatus(){
        PlatformStatusResponse response = adminService.getPlatformStatus();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<PageResponse<AdminUserSummaryResponse>>
    getAllUsers(@PageableDefault(page = 0,size = 15,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        PageResponse<AdminUserSummaryResponse> pageResponse = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(pageResponse);
    }

    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<AdminUserSummaryResponse> toggleStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(adminService.toggleUserStatus(id,userDetails.getUsername()));
    }

    @PatchMapping("/jobs/{id}/close-force")
    public ResponseEntity<JobResponse> closeForce(@PathVariable Long id){
        return ResponseEntity.ok(adminService.forceCloseJob(id));
    }








}
