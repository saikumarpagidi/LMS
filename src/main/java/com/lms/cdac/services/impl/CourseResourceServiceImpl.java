package com.lms.cdac.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.repsitories.CourseRepository;
import com.lms.cdac.repsitories.CourseResourceRepository;
import com.lms.cdac.repsitories.CourseTopicRepository;
import com.lms.cdac.services.CourseResourceService;

@Service
public class CourseResourceServiceImpl implements CourseResourceService {

    private final CourseResourceRepository courseResourceRepository;
    private final CourseTopicRepository courseTopicRepository;
    private final CourseRepository courseRepository;

    // Injected from application.properties (spring.servlet.multipart.location)
    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "image/png",
            "image/jpeg",
            "video/mp4"
    );

    public CourseResourceServiceImpl(CourseResourceRepository courseResourceRepository,
                                     CourseTopicRepository courseTopicRepository,
                                     CourseRepository courseRepository) {
        this.courseResourceRepository = courseResourceRepository;
        this.courseTopicRepository = courseTopicRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<CourseResource> getResourcesByCourse(Integer courseId) {
        return courseResourceRepository.findByCourseId(courseId);
    }

    @Override
    public List<CourseResource> getResourcesByTopic(Integer topicId) {
        return courseResourceRepository.findByCourseTopicId(topicId);
    }

    @Override
    public CourseResource saveResource(Integer topicId, MultipartFile file) {
        String fileType = file.getContentType();
        if (!ALLOWED_TYPES.contains(fileType)) {
            throw new RuntimeException("❌ File type not allowed. Allowed types: PDF, PPT, PNG, JPEG, MP4");
        }

        CourseTopic courseTopic = courseTopicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Course topic not found"));
        Course course = courseTopic.getCourse();

        try {
            // Create a safe filename (replace spaces with underscores)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
            
            // Ensure upload directory exists
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath);
            }

            // Save the file to the upload directory
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved file to: " + filePath);

            // Create and save the CourseResource entity
            CourseResource resource = new CourseResource();
            resource.setCourseTopic(courseTopic);
            resource.setCourse(course);
            resource.setFileName(fileName);
            resource.setFileUrl("/uploads/" + fileName);
            resource.setFileType(fileType);
            resource.setFileSize(file.getSize());

            return courseResourceRepository.save(resource);
        } catch (IOException e) {
            throw new RuntimeException("❌ File upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCourseNameById(Integer courseId) {
        return courseRepository.findById(courseId)
                .map(Course::getCourseName)
                .orElse(null);
    }

    @Override
    public String getResourcePathById(Integer resourceId) {
        CourseResource resource = courseResourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return resource.getFileUrl();
    }

    @Override
    public Optional<CourseResource> getResourceById(Integer resourceId) {
        return courseResourceRepository.findById(resourceId);
    }
}