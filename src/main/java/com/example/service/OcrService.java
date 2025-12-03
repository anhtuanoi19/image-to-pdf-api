package com.example.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService() {
        tesseract = new Tesseract();

        tesseract.setDatapath("C:\\Users\\Admin\\Desktop\\Development\\langue-link\\tessdata");  // thư mục chứa eng.traineddata, vie.traineddata
        tesseract.setLanguage("eng+vie");

        // Tránh lỗi Invalid memory access
        tesseract.setOcrEngineMode(1);
        tesseract.setPageSegMode(1);
    }

    public String extractText(File imageFile) {
        try {
            return tesseract.doOCR(imageFile);
        } catch (Exception e) {
            return ""; // tránh crash PDF
        }
    }
}