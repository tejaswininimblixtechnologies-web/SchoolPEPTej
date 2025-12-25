package com.nimblix.SchoolPEPProject.Controller;

import com.nimblix.SchoolPEPProject.Request.ClassroomRequest;
import com.nimblix.SchoolPEPProject.Request.TeacherEditProfileRequest;
import com.nimblix.SchoolPEPProject.Request.TeacherRegistrationRequest;
import com.nimblix.SchoolPEPProject.Response.TeacherDetailsResponse;
import com.nimblix.SchoolPEPProject.Response.TeacherProfileResponse;
import com.nimblix.SchoolPEPProject.Service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/teacherRegister")
    public ResponseEntity<Map<String, String>> registerTeacher(
            @RequestBody TeacherRegistrationRequest request) {
        return ResponseEntity.ok(teacherService.registerTeacher(request));
    }



    @GetMapping("/getTeacher")
    public ResponseEntity<TeacherDetailsResponse> getTeacherDetails(
            @RequestParam Long teacherId) {

        return ResponseEntity.ok(
                teacherService.getTeacherDetails(teacherId)
        );
    }

    @PutMapping(
            value = "/updateTeacher",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, String> updateTeacherDetails(
            @RequestBody TeacherRegistrationRequest request,
            @RequestParam Long teacherId) {

        return teacherService.updateTeacherDetails(request, teacherId);
    }

    @DeleteMapping("/delete")
    public Map<String, String> deleteTeacherRecord(
            @RequestParam Long teacherId,
            @RequestParam Long schoolId) {

        return teacherService.deleteTeacherDetails(teacherId, schoolId);
    }

    @PostMapping("/createClassroom")
    public ResponseEntity<Map<String, String>> createClassroom(
            @RequestBody ClassroomRequest request) {

        return teacherService.createClassroom(request);
    }

    @GetMapping("/profile")
    public ResponseEntity<TeacherProfileResponse> viewTeacherProfile(
            @RequestParam Long teacherId,
            @RequestParam Long schoolId) {

        return ResponseEntity.ok(
                teacherService.getTeacherProfile(teacherId, schoolId)
        );
    }

    @PutMapping("/editProfile")
    public ResponseEntity<Map<String, String>> editTeacherProfile(
            @RequestParam Long teacherId,
            @RequestParam Long schoolId,
            @RequestBody TeacherEditProfileRequest request) {

        return ResponseEntity.ok(
                teacherService.editTeacherProfile(teacherId, schoolId, request)
        );
    }

    @DeleteMapping("/deleteProfile")
    public ResponseEntity<Map<String, String>> deleteTeacherProfile(
            @RequestParam Long teacherId,
            @RequestParam Long schoolId) {

        return ResponseEntity.ok(
                teacherService.deleteTeacherProfile(teacherId, schoolId)
        );
    }
}
