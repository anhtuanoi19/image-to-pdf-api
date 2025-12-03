package com.example.service;

import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private OcrService ocrService;

    // ==========================
    // OCR PDF với font Unicode
    // ==========================
    public byte[] generatePdfWithOcr(List<File> imageFiles) throws IOException, TesseractException {

        try (PDDocument document = new PDDocument()) {

            // Load font Unicode (Arial)
            PDType0Font font = PDType0Font.load(document, new File("C:\\Windows\\Fonts\\arial.ttf"));

            for (File imageFile : imageFiles) {

                String extractedText = ocrService.extractText(imageFile);

                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), document);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Resize ảnh để vừa A4
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float imgW = pdImage.getWidth();
                float imgH = pdImage.getHeight();
                float scale = Math.min(pageWidth / imgW, (pageHeight - 100) / imgH);

                contentStream.drawImage(pdImage, 50, pageHeight - imgH * scale - 50,
                        imgW * scale, imgH * scale);

                // Thêm text OCR Unicode
//                contentStream.beginText();
//                contentStream.setFont(font, 10);
//                contentStream.newLineAtOffset(50, 30);
//                String preview = extractedText.length() > 200 ? extractedText.substring(0, 200) + "..." : extractedText;
//                contentStream.showText(preview.replace("\n", " "));
//                contentStream.endText();
//
                contentStream.close();
            }

            // Xuất PDF ra mảng byte
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.save(baos);
                return baos.toByteArray();
            }
        }
    }

    // ==========================
    // Non-OCR PDF
    // ==========================
    public byte[] generatePdfWithoutOcr(List<File> imageFiles) throws IOException {

        try (PDDocument document = new PDDocument()) {

            for (File imageFile : imageFiles) {
                PDImageXObject img = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), document);
                PDPage page = new PDPage(new PDRectangle(img.getWidth(), img.getHeight()));
                document.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    cs.drawImage(img, 0, 0, img.getWidth(), img.getHeight());
                }
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.save(baos);
                return baos.toByteArray();
            }
        }
    }
}
