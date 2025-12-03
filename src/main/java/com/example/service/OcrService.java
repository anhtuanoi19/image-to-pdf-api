package com.example.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService() {
        this.tesseract = new Tesseract();
        // Đường dẫn đến thư mục tessdata (cần download từ https://github.com/tesseract-ocr/tessdata)
        // Mặc định sẽ tìm trong /usr/local/share/tessdata (Mac) hoặc C:\Program Files\Tesseract-OCR\tessdata (Windows)
        // tesseract.setDatapath("/path/to/tessdata");
        tesseract.setLanguage("eng+vie"); // Hỗ trợ tiếng Anh và tiếng Việt
    }

    public String extractText(File imageFile) throws TesseractException {
        return tesseract.doOCR(imageFile);
    }
}