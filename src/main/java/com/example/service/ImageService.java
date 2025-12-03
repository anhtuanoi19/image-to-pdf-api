package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@Service
public class ImageService {

    public BufferedImage processImage(MultipartFile imageFile) throws IOException {
        return ImageIO.read(imageFile.getInputStream());
    }
}