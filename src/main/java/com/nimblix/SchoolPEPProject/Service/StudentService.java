package com.nimblix.SchoolPEPProject.Service;

import com.nimblix.SchoolPEPProject.Request.StudentRegistrationRequest;
import com.nimblix.SchoolPEPProject.Response.StudentDetailsResponse;
import com.nimblix.SchoolPEPProject.Response.StudentResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StudentService {

    // Register student
    ResponseEntity<?> registerStudent(StudentRegistrationRequest request);

    // Update student details
    void updateStudentDetails(Long studentId, StudentRegistrationRequest request);

    //  delete stud
    void deleteStudent(Long studentId);

    // Get students by schoolId
    List<StudentDetailsResponse> getStudentsBySchoolId(Long schoolId);

    //  Get single student profile
    StudentResponse getStudentProfile(Long studentId);
}
