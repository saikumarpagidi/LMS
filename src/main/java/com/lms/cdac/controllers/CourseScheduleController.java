package com.lms.cdac.controllers;

import com.lms.cdac.entities.CourseSchedule;
import com.lms.cdac.entities.Institution;
import com.lms.cdac.services.CourseScheduleService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.InstitutionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/course-schedules")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService courseScheduleService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private InstitutionService institutionService;

    /**
     * Show course schedule creation form with filtered resource centers based on location.
     */
    @GetMapping("/create")
    public String createCourseScheduleForm(@RequestParam(name = "location", required = false) String location, Model model) {
        model.addAttribute("courseSchedule", new CourseSchedule());
        model.addAttribute("courses", courseService.getAllCourses());



        // Fetch resource centers based on the selected location
        List<Institution> resourceCenters;
        if (location != null && !location.isEmpty()) {
            resourceCenters = institutionService.getInstitutionsByLocation(location);
        } else {
            resourceCenters = institutionService.getAllInstitutions();
        }
        
        // Debugging logs to check if the correct resource centers are fetched
        System.out.println("Received location: " + location);
        System.out.println("Filtered Resource Centers: " + resourceCenters);
        
        model.addAttribute("resourceCenters", resourceCenters);
        model.addAttribute("selectedLocation", location); // Store selected location
        return "user/course-shedule-create";
    }

    /**
     * Handle course schedule creation form submission.
     */
    @PostMapping("/create")
    public String createCourseSchedule(@ModelAttribute("courseSchedule") CourseSchedule courseSchedule) {
        courseScheduleService.createCourseSchedule(courseSchedule);
        return "redirect:/course-schedules/list";
    }

    /**
     * List all course schedules.
     */
    @GetMapping("/list")
    public String listCourseSchedules(Model model) {
        model.addAttribute("courseSchedules", courseScheduleService.getAllCourseSchedules());
        return "user/course-shedule-list";
    }

    /**
     * Edit an existing course schedule.
     */
    @GetMapping("/edit/{id}")
    public String editCourseSchedule(@PathVariable Integer id, Model model) {
        model.addAttribute("courseSchedule", courseScheduleService.getCourseScheduleById(id));
        return "user/course-shedule-edit";
    }

    /**
     * Update an existing course schedule.
     */
    @PostMapping("/edit/{id}")
    public String updateCourseSchedule(@PathVariable Integer id, @ModelAttribute("courseSchedule") CourseSchedule courseSchedule) {
        courseSchedule.setId(id);
        courseScheduleService.updateCourseSchedule(courseSchedule);
        return "redirect:/course-schedules/list";
    }

    /**
     * Delete a course schedule.
     */
    @GetMapping("/delete/{id}")
    public String deleteCourseSchedule(@PathVariable Integer id) {
        courseScheduleService.deleteCourseSchedule(id);
        return "redirect:/course-schedules/list";
    }
}