package com.nimblix.SchoolPEPProject.ServiceImpl;

import com.nimblix.SchoolPEPProject.Constants.SchoolConstants;
import com.nimblix.SchoolPEPProject.Exception.UserNotFoundException;
import com.nimblix.SchoolPEPProject.Model.*;

import com.nimblix.SchoolPEPProject.Repository.*;
import com.nimblix.SchoolPEPProject.Request.AssignmentShareRequest;
import com.nimblix.SchoolPEPProject.Request.ClassroomRequest;
import com.nimblix.SchoolPEPProject.Request.TeacherRegistrationRequest;
import com.nimblix.SchoolPEPProject.Response.TeacherDetailsResponse;
import com.nimblix.SchoolPEPProject.Service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClassroomRepository classroomRepository;
    private final PasswordEncoder passwordEncoder;

    //--------------------Assignment Sharing--------------------//
    private final AssignmentsRepository assignmentRepository;
    private final StudentRepository studentRepository;



    Map<String, String> response = new HashMap<>();

    @Override
    public Map<String, String> registerTeacher(TeacherRegistrationRequest request) {

        Map<String, String> response = new HashMap<>();

        if (request.getFirstName() == null || request.getFirstName().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            response.put(SchoolConstants.MESSAGE,
                    "Missing required fields (firstName, email, password)");
            return response;
        }

        if (teacherRepository.existsByEmailId(request.getEmail())) {
            response.put(SchoolConstants.MESSAGE, "Teacher already exists with this email");
            return response;
        }

        Role teacherRole = roleRepository.findByRoleName(SchoolConstants.TEACHER_ROLE);

        // ✅ Create ONLY Teacher
        Teacher teacher = new Teacher();
        teacher.setPrefix(request.getPrefix());
        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setEmailId(request.getEmail());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));
        teacher.setSchoolId(1L); // TODO: from logged-in admin

        // inherited from User
        teacher.setRole(teacherRole);
        teacher.setDesignation(SchoolConstants.TEACHER_ROLE);
        teacher.setStatus(SchoolConstants.ACTIVE);
        teacher.setIsLogin(false);

        teacherRepository.save(teacher);

        response.put(SchoolConstants.MESSAGE, "Teacher Registered Successfully!");
        return response;
    }

//    @Override
//    public ResponseEntity<Teacher> getTeacherDetails(Long teacherId) {
//
//        if (teacherId == null) {
//            throw new IllegalArgumentException("Teacher ID must not be null");
//        }
//
//        return teacherRepository.findById(teacherId)
//                .orElseThrow(() ->
//                        new UserNotFoundException("Teacher not found with id: " + teacherId));
//    }



    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public ResponseEntity<Map<String, String>> createClassroom(ClassroomRequest request) {

        Map<String, String> response = new HashMap<>();

        List<Classroom> existing = classroomRepository
                .findByClassroomNameAndSchoolId(request.getClassroomName(), request.getSchoolId());

        if (!existing.isEmpty()) {
            response.put("status", "FAIL");
            response.put("message", "Classroom already exists for this school");
            return ResponseEntity.status(409).body(response); // 🔥409 Conflict
        }

        Classroom classroom = new Classroom();
        classroom.setClassroomName(request.getClassroomName());
        classroom.setSchoolId(request.getSchoolId());
        classroom.setTeacherId(request.getTeacherId());
        classroom.setSubject(request.getSubject());
        classroomRepository.save(classroom);

        response.put("status", "SUCCESS");
        response.put("message", "Classroom created successfully");
        return ResponseEntity.ok(response); // 200
    }

    @Override
    public TeacherDetailsResponse getTeacherDetails(Long teacherId) {

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException("Teacher ID must be a positive number");
        }

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Teacher not found with id: " + teacherId
                        ));

        return TeacherDetailsResponse.builder()
                .id(teacher.getId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .emailId(teacher.getEmailId())
                .mobile(teacher.getMobile())
                .prefix(teacher.getPrefix())
                .designation(teacher.getDesignation())
                .gender(teacher.getGender())
                .status(teacher.getStatus())
                .build();
    }

    @Override
    public Map<String, String> updateTeacherDetails(TeacherRegistrationRequest request, Long teacherId) {
        Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);

        Map<String, String> response = new HashMap<>();

        if (teacherOptional.isEmpty()) {
            response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_ERORR);
            response.put(SchoolConstants.MESSAGE, "Teacher not found");
            return response;
        }

        Teacher teacher = teacherOptional.get();
        teacher.setPrefix(request.getPrefix());
        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setEmailId(request.getEmail());
        teacher.setSchoolId(request.getSchoolId());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));
        teacherRepository.save(teacher);

        response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_SUCCESS);
        response.put(SchoolConstants.MESSAGE, "Teacher details updated successfully");
        return response;
    }

    @Override
    public Map<String, String> deleteTeacherDetails(Long teacherId, Long schoolId) {

        Teacher teacher= teacherRepository.findByTeacherIdAndSchoolId(teacherId,schoolId);
        teacherRepository.delete(teacher);
        response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_SUCCESS);
        response.put(SchoolConstants.MESSAGE, "Teacher details deleted successfully");
        return response;
    }

    //-------------Assignment Share Service Implementation ----------------
    @Override
    public Map<String, String> shareAssignment(
            Long assignmentId,
            AssignmentShareRequest request) {

        Map<String, String> response = new HashMap<>();

        if (assignmentId == null || assignmentId <= 0) {
            response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_ERORR);
            response.put(SchoolConstants.MESSAGE, "Assignment ID is required");
            return response;
        }

        if (request.getClassId() == null) {
            response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_ERORR);
            response.put(SchoolConstants.MESSAGE, "classId is required");
            return response;
        }

        if (request.getSection() == null || request.getSection().isBlank()) {
            response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_ERORR);
            response.put(SchoolConstants.MESSAGE, "section is required");
            return response;
        }

        Optional<Assignments> assignmentOptional =
                assignmentRepository.findById(assignmentId);

        if (assignmentOptional.isEmpty()) {
            response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_ERORR);
            response.put(SchoolConstants.MESSAGE, "Assignment not found");
            return response;
        }

        List<Student> students =
                studentRepository.findByClassIdAndSection(
                        request.getClassId(),
                        request.getSection()
                );

        if (students.isEmpty()) {
            response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_ERORR);
            response.put(SchoolConstants.MESSAGE,
                    "No students found for given class and section");
            return response;
        }

        Assignments assignment = assignmentOptional.get();
        assignment.setShared(true);
        assignmentRepository.save(assignment);

        response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_SUCCESS);
        response.put(SchoolConstants.MESSAGE,
                "Assignment shared successfully with students");

        return response;
    }

}
