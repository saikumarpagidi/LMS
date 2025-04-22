package com.lms.cdac.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.repsitories.CourseRepository;
import com.lms.cdac.repsitories.CourseResourceRepository;
import com.lms.cdac.repsitories.CourseTopicRepository;
import com.lms.cdac.services.CourseResourceService;

@Service
public class CourseResourceServiceImpl implements CourseResourceService {

    @Autowired
    private CourseResourceRepository courseResourceRepository;

    @Autowired
    private CourseTopicRepository courseTopicRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static final String UPLOAD_DIR = "C:/lms/uploads/";

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/vnd.ms-powerpoint",
            "image/png",
            "image/jpeg",
            "video/mp4"
    );

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
        // Validate file type
        String fileType = file.getContentType();
        if (!ALLOWED_TYPES.contains(fileType)) {
            throw new RuntimeException("❌ File type not allowed. Allowed types: PDF, PPT, PNG, JPEG, MP4");
        }

        // Get course topic and course
        CourseTopic courseTopic = courseTopicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Course topic not found"));
        Course course = courseTopic.getCourse();

        try {
            // Generate a unique file name
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
            Path uploadPath = Paths.get(UPLOAD_DIR);
            
            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create the CourseResource entity
            CourseResource resource = new CourseResource();
            resource.setCourseTopic(courseTopic);
            resource.setCourse(course);
            resource.setFileName(fileName);
            resource.setFileUrl("/uploads/" + fileName);
            resource.setFileType(fileType);
            resource.setFileSize(file.getSize());

            // Save the resource
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
        
        return resource.getFileUrl();  // Assuming `filePath` column stores the file path
    }
    @Override
    public Optional<CourseResource> getResourceById(Integer resourceId) {
        return courseResourceRepository.findById(resourceId);
    }

}