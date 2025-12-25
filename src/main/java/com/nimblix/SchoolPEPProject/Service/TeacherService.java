package com.nimblix.SchoolPEPProject.Service;

import com.nimblix.SchoolPEPProject.Request.ClassroomRequest;
import com.nimblix.SchoolPEPProject.Request.TeacherEditProfileRequest;
import com.nimblix.SchoolPEPProject.Request.TeacherRegistrationRequest;
import com.nimblix.SchoolPEPProject.Response.TeacherDetailsResponse;
import com.nimblix.SchoolPEPProject.Response.TeacherProfileResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface TeacherService {
    Map<String, String> registerTeacher(TeacherRegistrationRequest request);

//    ResponseEntity<Teacher> getTeacherDetails(Long teacherId);

    ResponseEntity<Map<String, String>> createClassroom(ClassroomRequest request);

    public TeacherDetailsResponse getTeacherDetails(Long teacherId);

    Map<String, String> updateTeacherDetails(TeacherRegistrationRequest request, Long teacherId);

    Map<String, String> deleteTeacherDetails(Long teacherId, Long schoolId);

    TeacherProfileResponse getTeacherProfile(Long teacherId, Long schoolId);

    Map<String, String> editTeacherProfile(
            Long teacherId, Long schoolId, TeacherEditProfileRequest request);

    Map<String, String> deleteTeacherProfile(Long teacherId, Long schoolId);
}

