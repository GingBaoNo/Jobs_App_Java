package com.example.demo.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static String saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        // Sử dụng thư mục làm việc hiện tại của ứng dụng để tạo đường dẫn tuyệt đối
        String projectPath = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectPath, uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileCode = System.currentTimeMillis() / 1000 + "_" + fileName;
        Path filePath = uploadPath.resolve(fileCode);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Could not save file: " + fileName, ex);
        }

        return fileCode;
    }

    public static boolean deleteFile(String uploadDir, String fileName) {
        // Sử dụng thư mục làm việc hiện tại của ứng dụng để tạo đường dẫn tuyệt đối
        String projectPath = System.getProperty("user.dir");
        Path filePath = Paths.get(projectPath, uploadDir, fileName);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}