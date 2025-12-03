package com.example.service;

import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
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
    public byte[] generatePdfWithOcr(List<File> imageFiles) throws IOException {

        try (PDDocument document = new PDDocument()) {

            // Font Unicode (Arial)
            PDType0Font font = PDType0Font.load(document, new File("C:\\Windows\\Fonts\\arial.ttf"));

            for (File imageFile : imageFiles) {

                // OCR text full image
                String text = ocrService.extractText(imageFile);

                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                PDPageContentStream cs = new PDPageContentStream(document, page);

                // ===== Draw image on PDF =====
                PDImageXObject img = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), document);

                float pw = page.getMediaBox().getWidth();
                float ph = page.getMediaBox().getHeight();

                float iw = img.getWidth();
                float ih = img.getHeight();

                float scale = Math.min(pw / iw, ph / ih);

                float w = iw * scale;
                float h = ih * scale;

                cs.drawImage(img, (pw - w) / 2, (ph - h) / 2, w, h);

                // ===== Add invisible OCR text layer =====
                cs.beginText();
                cs.setFont(font, 10);

                cs.setTextMatrix(new Matrix(0, 0, 0, 0, 10, 10));

                String clean = text.replace("\n", " ");
                cs.showText(clean);
                cs.endText();


                cs.close();
            }

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
