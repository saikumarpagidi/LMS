package com.lms.cdac.services.impl;

import com.lms.cdac.entities.MOM;
import com.lms.cdac.repsitories.MOMRepository;
import com.lms.cdac.services.MOMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class MOMServiceImpl implements MOMService {

    private final String uploadDir = "uploads/mom/";
    private final Path fileStoragePath = Paths.get(uploadDir); // 🔹 Fixed missing variable

    @Autowired
    private MOMRepository momRepository;

    @Override
    public List<MOM> getAllMOMs() {
        return momRepository.findAll();
    }

    @Override
    public MOM getMOMById(Integer id) {  // 🔹 Removed Integer ID method to avoid duplicate
        return momRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MOM not found for ID: " + id));
    }

    @Override
    public MOM saveMOM(String title, String description, MultipartFile file) {
        try {
            if (!Files.exists(fileStoragePath)) {
                Files.createDirectories(fileStoragePath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = fileStoragePath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            MOM mom = new MOM();
            mom.setTitle(title);
            mom.setDescription(description);
            mom.setFilePath(fileName);

            return momRepository.save(mom);
        } catch (Exception e) {
            throw new RuntimeException("File upload failed!", e);
        }
    }

    @Override
    public Resource loadMOMFile(String fileName) {
        try {
            Path filePath = fileStoragePath.resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading file: " + fileName, e);
        }
    }
}
