package com.lms.cdac.repsitories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.cdac.entities.LiveClassSchedule;

public interface LiveClassScheduleRepository extends JpaRepository<LiveClassSchedule, Integer> {

    List<LiveClassSchedule> findByResourceCenterName(String resourceCenterName);

    List<LiveClassSchedule> findByCourse_IdIn(List<Integer> courseIds);

    // ✅ Corrected method name to use 'course_Id'
    List<LiveClassSchedule> findByCourse_IdAndStartTimeAfterOrderByStartTimeAsc(Integer courseId, LocalDateTime time);
    List<LiveClassSchedule> findByEndTimeAfterOrderByStartTimeAsc(LocalDateTime now);
}
