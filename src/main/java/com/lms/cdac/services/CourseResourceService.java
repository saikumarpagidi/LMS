package com.lms.cdac.services;

import org.springframework.web.multipart.MultipartFile;
import com.lms.cdac.entities.CourseResource;
import java.util.List;

public interface CourseResourceService {
    List<CourseResource> getResourcesByCourse(Integer courseId);
    List<CourseResource> getResourcesByTopic(Integer topicId);
    CourseResource saveResource(Integer topicId, MultipartFile file);
    String getCourseNameById(Integer courseId);
    public String getResourcePathById(Integer resourceId);

}
