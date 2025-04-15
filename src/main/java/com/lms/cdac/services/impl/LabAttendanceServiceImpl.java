package com.lms.cdac.services.impl;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.LabAttendance;
import com.lms.cdac.entities.User;
import com.lms.cdac.repsitories.LabAttendanceRepository;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.LabAttendanceService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LabAttendanceServiceImpl implements LabAttendanceService {

    @Autowired
    private LabAttendanceRepository labAttendanceRepository;
    
    @Autowired
    private UserRepositories userRepository;
    
    @Autowired
    private CourseService courseService;

    @Override
    public List<LabAttendance> getAttendanceByStudent(User student) {
        return labAttendanceRepository.findByStudent(student);
    }

    @Override
    public List<LabAttendance> getAttendanceByCourse(Course course) {
        return labAttendanceRepository.findByCourse(course);
    }

    @Override
    public List<LabAttendance> getAttendanceByStudentAndCourse(User student, Course course) {
        return labAttendanceRepository.findByStudentAndCourse(student, course);
    }

    @Override
    public List<LabAttendance> getAttendanceByFaculty(User faculty) {
        return labAttendanceRepository.findByFaculty(faculty);
    }

    @Override
    public LabAttendance saveAttendance(LabAttendance attendance) {
        return labAttendanceRepository.save(attendance);
    }

    @Override
    public Map<String, Object> processCSVUpload(MultipartFile file, User faculty) {
        Map<String, Object> result = new HashMap<>();
        List<LabAttendance> attendances = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            // Validate CSV headers
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            if (headerMap == null || !isValidHeaders(headerMap)) {
                result.put("success", false);
                result.put("message", "Invalid CSV format. Required headers: Student Name, Course Name, Practical Name, Marks, Attendance Status");
                return result;
            }
            
            // Process each record
            for (CSVRecord record : csvParser) {
                try {
                    String studentName = record.get("Student Name");
                    String courseName = record.get("Course Name");
                    String practicalName = record.get("Practical Name");
                    String marksStr = record.get("Marks");
                    String attendanceStatus = record.get("Attendance Status");
                    
                    // Validate required fields
                    if (studentName == null || courseName == null || practicalName == null || 
                        marksStr == null || attendanceStatus == null) {
                        errors.add("Row " + record.getRecordNumber() + ": Missing required fields");
                        continue;
                    }
                    
                    // Validate attendance status
                    if (!attendanceStatus.equals("PRESENT") && !attendanceStatus.equals("ABSENT")) {
                        errors.add("Row " + record.getRecordNumber() + ": Invalid attendance status. Must be 'PRESENT' or 'ABSENT'");
                        continue;
                    }
                    
                    // Find student by name
                    Optional<User> studentOpt = userRepository.findByName(studentName);
                    if (studentOpt.isEmpty()) {
                        errors.add("Row " + record.getRecordNumber() + ": Student not found: " + studentName);
                        continue;
                    }
                    
                    // Find course by name
                    List<Course> courses = courseService.getAllCourses();
                    Optional<Course> courseOpt = courses.stream()
                            .filter(c -> c.getCourseName().equals(courseName))
                            .findFirst();
                    
                    if (courseOpt.isEmpty()) {
                        errors.add("Row " + record.getRecordNumber() + ": Course not found: " + courseName);
                        continue;
                    }
                    
                    // Parse marks
                    int marks;
                    try {
                        marks = Integer.parseInt(marksStr);
                        if (marks < 0 || marks > 100) {
                            errors.add("Row " + record.getRecordNumber() + ": Marks must be between 0 and 100");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        errors.add("Row " + record.getRecordNumber() + ": Invalid marks format: " + marksStr);
                        continue;
                    }
                    
                    // Create unique key for duplicate check
                    String uniqueKey = createUniqueKey(studentOpt.get().getUserId(), courseOpt.get().getId(), practicalName);
                    
                    // Check for duplicates
                    if (isDuplicatePractical(uniqueKey)) {
                        errors.add("Row " + record.getRecordNumber() + ": Duplicate practical data for: " + 
                                  studentName + " - " + courseName + " - " + practicalName);
                        continue;
                    }
                    
                    // Create attendance record
                    LabAttendance attendance = new LabAttendance();
                    attendance.setStudent(studentOpt.get());
                    attendance.setCourse(courseOpt.get());
                    attendance.setPracticalName(practicalName);
                    attendance.setMarks(attendanceStatus.equals("PRESENT") ? marks : 0);
                    attendance.setPresent(attendanceStatus.equals("PRESENT"));
                    attendance.setAttendanceStatus(attendanceStatus);
                    attendance.setFaculty(faculty);
                    attendance.setUploadDate(LocalDateTime.now());
                    attendance.setUniquePracticalKey(uniqueKey);
                    attendance.setCourseName(courseName);
                    attendance.setStudentName(studentName);
                    
                    attendances.add(attendance);
                    
                } catch (Exception e) {
                    errors.add("Row " + record.getRecordNumber() + ": Error processing record: " + e.getMessage());
                }
            }
            
            // Save valid records
            if (!attendances.isEmpty()) {
                labAttendanceRepository.saveAll(attendances);
            }
            
            // Prepare result
            result.put("success", errors.isEmpty());
            result.put("message", errors.isEmpty() ? 
                    "Successfully uploaded " + attendances.size() + " attendance records" : 
                    "Uploaded with errors: " + attendances.size() + " records saved, " + errors.size() + " errors");
            result.put("errors", errors);
            
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "Error processing CSV file: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public boolean isDuplicatePractical(String uniqueKey) {
        return !labAttendanceRepository.findByUniquePracticalKey(uniqueKey).isEmpty();
    }
    
    @Override
    public List<LabAttendance> getAttendanceByCourseIdAndStudentId(Integer courseId, String studentId) {
        return labAttendanceRepository.findByCourseIdAndStudentId(courseId, studentId);
    }
    
    private boolean isValidHeaders(Map<String, Integer> headerMap) {
        Set<String> requiredHeaders = new HashSet<>(Arrays.asList(
                "Student Name", "Course Name", "Practical Name", "Marks", "Attendance Status"
        ));
        
        return headerMap.keySet().containsAll(requiredHeaders);
    }
    
    private String createUniqueKey(String studentId, Integer courseId, String practicalName) {
        return studentId + "_" + courseId + "_" + practicalName;
    }
} 