package com.lms.cdac.services.impl;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.repsitories.CourseModuleRepository;
import com.lms.cdac.repsitories.CourseRepository;  
import com.lms.cdac.repsitories.CourseResourceRepository;
import com.lms.cdac.repsitories.CourseTopicRepository;
import com.lms.cdac.services.CourseTopicService;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Service
@Transactional
public class CourseTopicServiceImpl implements CourseTopicService {

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private CourseTopicRepository courseTopicRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseResourceRepository courseResourceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CourseTopic> getTopicsByModule(Integer moduleId) {
        return courseTopicRepository.findByCourseModule_Id(moduleId);
    }

    @Override
    public CourseTopic addTopic(Integer moduleId, String topicName) {
        CourseModule courseModule = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("❌ Module not found for ID: " + moduleId));

        Course course = courseModule.getCourse();
        if (course == null) {
            throw new RuntimeException("❌ The module does not have an associated course.");
        }

        CourseTopic courseTopic = new CourseTopic();
        courseTopic.setTopicName(topicName);
        courseTopic.setCourseModule(courseModule);
        courseTopic.setCourse(course);  // ✅ FIX: Now setting the Course field

        return courseTopicRepository.save(courseTopic);
    }

    @Override
    public Integer getCourseIdByTopicId(Integer topicId) {
        return courseTopicRepository.findById(topicId)
            .map(courseTopic -> {
                CourseModule module = courseTopic.getCourseModule();
                if (module == null || module.getCourse() == null) {
                    throw new RuntimeException("❌ No associated course found for topic ID: " + topicId);
                }
                return module.getCourse().getId();
            })
            .orElseThrow(() -> new RuntimeException("❌ Topic not found for ID: " + topicId));
    }

    @Override
    public List<CourseTopic> getTopicsWithResources(Integer moduleId) {
        List<CourseTopic> topics = courseTopicRepository.findByCourseModule_Id(moduleId);
        
        // Explicitly fetch resources and set them to the topics
        topics.forEach(topic -> {
            List<CourseResource> resources = courseResourceRepository.findByCourseTopic(topic);
            topic.setResources(resources);  
        });

        return topics;
    }

    @Override
    public CourseTopic getTopicById(Integer topicId) {
        return courseTopicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found with ID: " + topicId));
    }
}