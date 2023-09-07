package com.restoreserve.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    public String saveImage(MultipartFile image, String uploadPath) throws IOException {
        String filename = StringUtils.cleanPath(image.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID() + "_" + filename;
        String destinationPath = System.getProperty("user.dir") + File.separator + "src/main/resources/static/uploads/"
                + uploadPath;
        Path imagePath = Paths.get(destinationPath, uniqueFileName);
        System.out.println(imagePath.toString());
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        return "uploads/" + uploadPath + "/" + uniqueFileName;
    }

    public void deleteImage(String path) {
        String filePath = System.getProperty("user.dir") + File.separator + "src/main/resources/static/"
                + path;
        File fileToDelete = new File(filePath);
        fileToDelete.delete();
    }
}
