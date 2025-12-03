package com.example.controller;

import com.example.service.PdfService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private PdfService pdfService;

    @PostMapping(value = "/convert-to-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> convertImagesToPdf(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "useOcr", defaultValue = "true") boolean useOcr) {
        
        List<File> tempFiles = new ArrayList<>();
        
        try {
            // Lưu các file upload vào thư mục tạm
            for (MultipartFile file : files) {
                Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
                file.transferTo(tempFile.toFile());
                tempFiles.add(tempFile.toFile());
            }

            // Tạo PDF
            File pdfFile;
            if (useOcr) {
                pdfFile = pdfService.generatePdfWithOcr(tempFiles);
            } else {
                pdfFile = pdfService.generatePdfWithoutOcr(tempFiles);
            }

            // Trả về file PDF
            Resource resource = new FileSystemResource(pdfFile);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFile.getName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (IOException | TesseractException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            // Xóa các file tạm
            tempFiles.forEach(File::delete);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Image to PDF API is running!");
    }
}