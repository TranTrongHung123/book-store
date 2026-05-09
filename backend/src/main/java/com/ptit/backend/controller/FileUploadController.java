package com.ptit.backend.controller;

import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadImage(file);
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .code(1000)
                    .message("Upload image successfully")
                    .result(url)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .code(9999)
                    .message("Could not upload image: " + e.getMessage())
                    .result(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
