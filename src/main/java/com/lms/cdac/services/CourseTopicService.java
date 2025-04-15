package com.lms.cdac.services;

import com.lms.cdac.entities.CourseTopic;
import java.util.List;

public interface CourseTopicService {
    List<CourseTopic> getTopicsByModule(Integer moduleId); // Get topics associated with a module
    CourseTopic addTopic(Integer moduleId, String topicName); // Add a new topic under a module
    Integer getCourseIdByTopicId(Integer topicId); // Get the course ID by topic ID
    List<CourseTopic> getTopicsWithResources(Integer moduleId); // Get topics with resources
    CourseTopic getTopicById(Integer topicId); // Get a topic by its ID
}
