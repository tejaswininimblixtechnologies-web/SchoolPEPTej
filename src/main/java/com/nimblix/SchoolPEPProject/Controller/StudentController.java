package com.nimblix.SchoolPEPProject.Controller;

import com.nimblix.SchoolPEPProject.Request.StudentRegistrationRequest;
import com.nimblix.SchoolPEPProject.Response.StudentDetailsResponse;
import com.nimblix.SchoolPEPProject.Response.StudentResponse;
import com.nimblix.SchoolPEPProject.Service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    //registre student
    @PostMapping("/register")
    public ResponseEntity<?> studentRegistration(
            @RequestBody StudentRegistrationRequest request) {
        return studentService.registerStudent(request);
    }

    // get studetn by id
    @GetMapping("/details")
    public ResponseEntity<List<StudentDetailsResponse>> getStudentsBySchoolId(
            @RequestParam Long schoolId) {
        return ResponseEntity.ok(
                studentService.getStudentsBySchoolId(schoolId)
        );
    }

    // update students
    @PutMapping("/update")
    public ResponseEntity<?> updateStudent(
            @RequestParam Long studentId,
            @RequestBody StudentRegistrationRequest request) {

        studentService.updateStudentDetails(studentId, request);
        return ResponseEntity.ok("Student updated successfully");
    }

    //delete student
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteStudent(
            @RequestParam Long studentId) {

        studentService.deleteStudent(studentId);
        return ResponseEntity.ok("Student deleted successfully");
    }

    //get student detail
    @GetMapping("/profile")
    public ResponseEntity<StudentResponse> getStudentProfile(
            @RequestParam Long studentId) {

        return ResponseEntity.ok(
                studentService.getStudentProfile(studentId)
        );
    }
}
