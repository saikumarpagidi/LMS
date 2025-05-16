// src/main/java/com/lms/cdac/services/impl/LiveClassScheduleServiceImpl.java
package com.lms.cdac.services.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.cdac.entities.LiveClassSchedule;
import com.lms.cdac.repsitories.LiveClassScheduleRepository;
import com.lms.cdac.services.LiveClassScheduleService;

@Service
public class LiveClassScheduleServiceImpl implements LiveClassScheduleService {

    @Autowired
    private LiveClassScheduleRepository repository;

    @Override
    public void createLiveClassSchedule(LiveClassSchedule schedule) {
        repository.save(schedule);
    }

    @Override
    public List<LiveClassSchedule> getAllLiveClassSchedules() {
        return repository.findAll();
    }

    @Override
    public LiveClassSchedule getLiveClassScheduleById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void updateLiveClassSchedule(LiveClassSchedule schedule) {
        repository.save(schedule);
    }

    @Override
    public void deleteLiveClassSchedule(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<LiveClassSchedule> getLiveClassesByResourceCenter(String resourceCenterName) {
        return repository.findByResourceCenterName(resourceCenterName);
    }

    @Override
    public List<LiveClassSchedule> getLiveClassesByCourseIds(List<Integer> courseIds) {
        return repository.findByCourse_IdIn(courseIds);
    }

    @Override
    public List<LiveClassSchedule> getLiveClassesByCourseId(Integer courseId) {
        return repository.findByCourse_IdIn(Collections.singletonList(courseId));
    }
    @Override
    public List<LiveClassSchedule> getUpcomingLiveClassesByCourseId(Integer courseId) {
        return repository.findByCourse_IdAndStartTimeAfterOrderByStartTimeAsc(courseId, LocalDateTime.now());
    }
    
    @Override
    public List<LiveClassSchedule> getActiveOrUpcomingLiveClassesByCourseId(Integer courseId) {
        List<LiveClassSchedule> all = repository.findByCourse_IdIn(Collections.singletonList(courseId));
        LocalDateTime now = LocalDateTime.now();
        return all.stream()
                  .filter(lc -> lc.getEndTime().isAfter(now))
                  .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                  .toList();
    }


}
