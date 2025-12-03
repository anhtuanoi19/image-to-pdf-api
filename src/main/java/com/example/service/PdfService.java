package com.example.service;

import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private OcrService ocrService;

    public File generatePdfWithOcr(List<File> imageFiles) throws IOException, TesseractException {
        PDDocument document = new PDDocument();

        try {
            for (File imageFile : imageFiles) {
                // OCR để trích xuất text từ ảnh
                String extractedText = ocrService.extractText(imageFile);

                // Tạo page mới
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                // Thêm ảnh vào PDF
                PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), document);
                
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                
                // Tính toán kích thước để fit vào A4
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float imageWidth = pdImage.getWidth();
                float imageHeight = pdImage.getHeight();
                
                float scale = Math.min(pageWidth / imageWidth, (pageHeight - 100) / imageHeight);
                float scaledWidth = imageWidth * scale;
                float scaledHeight = imageHeight * scale;
                
                // Vẽ ảnh
                contentStream.drawImage(pdImage, 50, pageHeight - scaledHeight - 50, scaledWidth, scaledHeight);
                
                // Thêm text đã OCR vào phía dưới (invisible layer cho searchable PDF)
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(50, 30);
                contentStream.showText("OCR Text: " + extractedText.substring(0, Math.min(50, extractedText.length())) + "...");
                contentStream.endText();
                
                contentStream.close();
            }

            // Lưu file PDF
            Path tempDir = Files.createTempDirectory("pdf-output");
            File pdfFile = new File(tempDir.toFile(), "output-" + System.currentTimeMillis() + ".pdf");
            document.save(pdfFile);

            return pdfFile;
        } finally {
            document.close();
        }
    }

    public File generatePdfWithoutOcr(List<File> imageFiles) throws IOException {
        PDDocument document = new PDDocument();

        try {
            for (File imageFile : imageFiles) {
                PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), document);
                
                PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
                contentStream.close();
            }

            Path tempDir = Files.createTempDirectory("pdf-output");
            File pdfFile = new File(tempDir.toFile(), "output-" + System.currentTimeMillis() + ".pdf");
            document.save(pdfFile);

            return pdfFile;
        } finally {
            document.close();
        }
    }
}