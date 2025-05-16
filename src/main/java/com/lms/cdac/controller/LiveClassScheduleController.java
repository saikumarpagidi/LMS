package com.lms.cdac.controller;

import com.lms.cdac.entities.LiveClassSchedule;
import com.lms.cdac.services.InstitutionService;
import com.lms.cdac.services.LiveClassScheduleService;
import com.lms.cdac.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/live-classes")
public class LiveClassScheduleController {

    @Autowired
    private LiveClassScheduleService liveClassScheduleService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/create")
    public String createLiveClassForm(
            @RequestParam(name = "location", required = false) String location,
            Model model) {

        model.addAttribute("liveClassSchedule", new LiveClassSchedule());
        model.addAttribute("courses", courseService.getAllCourses());

        model.addAttribute("resourceCenters",
            (location != null && !location.isEmpty())
              ? institutionService.getInstitutionsByLocation(location)
              : institutionService.getAllInstitutions()
        );
        model.addAttribute("selectedLocation", location);
        return "live-class-create";  // resolves to live-class-create.html
    }

    @PostMapping("/create")
    public String saveLiveClass(@ModelAttribute LiveClassSchedule liveClassSchedule) {
        liveClassScheduleService.createLiveClassSchedule(liveClassSchedule);
        return "redirect:/live-classes/list";
    }

    @GetMapping("/list")
    public String listLiveClasses(Model model) {
        model.addAttribute("liveClasses", liveClassScheduleService.getAllLiveClassSchedules());
        return "live-class-list";
    }

    @GetMapping("/edit/{id}")
    public String editLiveClass(@PathVariable Integer id, Model model) {
        model.addAttribute("liveClassSchedule", liveClassScheduleService.getLiveClassScheduleById(id));
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("resourceCenters", institutionService.getAllInstitutions());
        return "live-class-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateLiveClass(
            @PathVariable Integer id,
            @ModelAttribute LiveClassSchedule liveClassSchedule) {
        liveClassSchedule.setId(id);
        liveClassScheduleService.updateLiveClassSchedule(liveClassSchedule);
        return "redirect:/live-classes/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteLiveClass(@PathVariable Integer id) {
        liveClassScheduleService.deleteLiveClassSchedule(id);
        return "redirect:/live-classes/list";
    }
}
