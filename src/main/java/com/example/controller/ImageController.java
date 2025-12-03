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
    public ResponseEntity<byte[]> convertImagesToPdf(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "useOcr", defaultValue = "true") boolean useOcr) {

        List<File> tempFiles = new ArrayList<>();

        try {
            // Lưu ảnh tạm
            for (MultipartFile file : files) {
                Path temp = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
                file.transferTo(temp.toFile());
                tempFiles.add(temp.toFile());
            }

            // Sinh PDF ra byte[]
            byte[] pdfBytes = useOcr
                    ? pdfService.generatePdfWithOcr(tempFiles)
                    : pdfService.generatePdfWithoutOcr(tempFiles);

            // Trả về cho client
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } finally {
            // Xóa ảnh tạm
            tempFiles.forEach(File::delete);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Image to PDF API is running!");
    }
}