package com.lms.cdac.controller;

import com.lms.cdac.entities.MOM;
import com.lms.cdac.services.MOMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/mom")
public class MOMController {

    @Autowired
    private MOMService momService;

    // 📌 MOM Dashboard page show karne ke liye
    @GetMapping("/dashboard")
    public String showMOMDashboard(Model model) {
        List<MOM> momList = momService.getAllMOMs();
        model.addAttribute("momList", momList);
        return "user/mom_dashboard"; // HTML page render karega
    }

    // 📌 MOM ko save karne ka endpoint (Form ke through)
    @PostMapping("/save")
    public String saveMOM(@RequestParam("title") String title,
                          @RequestParam("description") String description,
                          @RequestParam("file") MultipartFile file) {
        momService.saveMOM(title, description, file);
        return "redirect:/mom/dashboard";
    }

    // 📌 MOM ko view karne ka endpoint
    @GetMapping("/view/{id}")
    public String viewMOM(@PathVariable Integer id, Model model) {
        MOM mom = momService.getMOMById(id);
        model.addAttribute("mom", mom);
        return "user/mom_view"; // MOM view page
    }

    // 📌 MOM ka file download karne ka endpoint
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadMOMFile(@PathVariable String fileName) {
        Resource file = momService.loadMOMFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    // 📌 MOM ka file direct view karne ka endpoint
    @GetMapping("/view/file/{fileName}")
    public ResponseEntity<Resource> viewMOMFile(@PathVariable String fileName) {
        Resource file = momService.loadMOMFile(fileName);
        String contentType = "application/octet-stream";

        if (fileName.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (fileName.endsWith(".mp4")) {
            contentType = "video/mp4";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
               .body(file);
    

    // 📌 MOM delete karne ka endpoint
   // @GetMapping("/delete/{id}")
   // public String deleteMOM(@PathVariable Long id) {
   //     momService.deleteMOM(id);
      //  return "redirect:/mom/dashboard";
    }
}
