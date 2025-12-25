package com.nimblix.SchoolPEPProject.ServiceImpl;

import com.nimblix.SchoolPEPProject.Constants.SchoolConstants;
import com.nimblix.SchoolPEPProject.Model.Role;
import com.nimblix.SchoolPEPProject.Model.Student;
import com.nimblix.SchoolPEPProject.Repository.RoleRepository;
import com.nimblix.SchoolPEPProject.Repository.StudentRepository;
import com.nimblix.SchoolPEPProject.Request.StudentRegistrationRequest;
import com.nimblix.SchoolPEPProject.Response.StudentDetailsResponse;
import com.nimblix.SchoolPEPProject.Response.StudentResponse;
import com.nimblix.SchoolPEPProject.Service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;

    //  Register Student
    @Override
    public ResponseEntity<?> registerStudent(StudentRegistrationRequest request) {

        if (!request.getPassword().equals(request.getReEnterPassword())) {
            return ResponseEntity.badRequest()
                    .body("Password and Re-Enter Password do not match!");
        }

        if (studentRepository.existsByEmailId(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already registered!");
        }

        Role studentRole = roleRepository.findByRoleName(SchoolConstants.STUDENT);

        Student student = new Student();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmailId(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setSchoolId(request.getSchoolId());
        student.setStatus(SchoolConstants.STATUS_ACTIVE);
        student.setIsLogin(Boolean.FALSE);
        student.setDesignation(SchoolConstants.STUDENT);
        student.setRole(studentRole);

        Student savedStudent = studentRepository.save(student);

        return ResponseEntity.ok(
                "Student registered successfully with ID: " + savedStudent.getId()
        );
    }

    //  Update Student
    @Override
    public void updateStudentDetails(Long studentId, StudentRegistrationRequest request) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with ID: " + studentId));

        if (request.getFirstName() != null) {
            student.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            student.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            student.setEmailId(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {

            if (!request.getPassword().equals(request.getReEnterPassword())) {
                throw new RuntimeException(
                        "Password and Re-Enter Password do not match!"
                );
            }

            student.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );
        }

        if (request.getSchoolId() != null) {
            student.setSchoolId(request.getSchoolId());
        }

        studentRepository.save(student);
    }

    //  Get students by schoolId
    @Override
    public List<StudentDetailsResponse> getStudentsBySchoolId(Long schoolId) {

        List<Student> students =
                studentRepository.findBySchoolId(schoolId);

        return students.stream()
                .map(student -> StudentDetailsResponse.builder()
                        .id(student.getId())
                        .firstName(student.getFirstName())
                        .lastName(student.getLastName())
                        .emailId(student.getEmailId())
                        .mobile(student.getMobile())
                        .schoolId(student.getSchoolId())
                        .status(student.getStatus())
                        .classId(student.getClassId())
                        .section(student.getSection())
                        .roleName(
                                student.getRole() != null
                                        ? student.getRole().getRoleName()
                                        : null
                        )
                        .build()
                )
                .toList();
    }

    //  Delete (Soft Delete)
    @Override
    public void deleteStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with ID: " + studentId));

        student.setStatus(SchoolConstants.STATUS_INACTIVE);
        studentRepository.save(student);
    }

    //  Get student profile
    @Override
    public StudentResponse getStudentProfile(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        return StudentResponse.builder()
                .studentId(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dob(student.getDateOfBirth())
                .mobileNo(student.getMobile())
                .emailId(student.getEmailId())
                .rollNo(
                        student.getRollNo() != null
                                ? student.getRollNo().toString()
                                : null
                )
                .admissionNo(
                        student.getAdmissionNo() != null
                                ? student.getAdmissionNo().toString()
                                : null
                )
                .registrationNo(
                        student.getRegistrationNo() != null
                                ? student.getRegistrationNo().toString()
                                : null
                )
                .section(student.getSection())
                .profileImage(student.getProfilePicture())
                .build();
    }
}
