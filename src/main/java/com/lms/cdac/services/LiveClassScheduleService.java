// src/main/java/com/lms/cdac/services/LiveClassScheduleService.java
package com.lms.cdac.services;

import java.util.List;
import com.lms.cdac.entities.LiveClassSchedule;

public interface LiveClassScheduleService {

    void createLiveClassSchedule(LiveClassSchedule schedule);

    List<LiveClassSchedule> getAllLiveClassSchedules();

    LiveClassSchedule getLiveClassScheduleById(Integer id);

    void updateLiveClassSchedule(LiveClassSchedule schedule);

    void deleteLiveClassSchedule(Integer id);

    List<LiveClassSchedule> getLiveClassesByResourceCenter(String resourceCenterName);

    /**
     * Fetch multiple courses at once.
     */
    List<LiveClassSchedule> getLiveClassesByCourseIds(List<Integer> courseIds);

    /**
     * Convenience: fetch live classes for exactly one course.
     */
    List<LiveClassSchedule> getLiveClassesByCourseId(Integer courseId);
    List<LiveClassSchedule> getUpcomingLiveClassesByCourseId(Integer courseId);
    List<LiveClassSchedule> getActiveOrUpcomingLiveClassesByCourseId(Integer courseId);


}
