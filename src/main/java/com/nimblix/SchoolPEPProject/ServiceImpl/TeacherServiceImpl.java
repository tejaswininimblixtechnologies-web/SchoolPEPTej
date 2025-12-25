package com.nimblix.SchoolPEPProject.ServiceImpl;

import com.nimblix.SchoolPEPProject.Constants.SchoolConstants;
import com.nimblix.SchoolPEPProject.Exception.UserNotFoundException;
import com.nimblix.SchoolPEPProject.Model.Classroom;
import com.nimblix.SchoolPEPProject.Model.Role;
import com.nimblix.SchoolPEPProject.Model.Teacher;
import com.nimblix.SchoolPEPProject.Repository.ClassroomRepository;
import com.nimblix.SchoolPEPProject.Repository.RoleRepository;
import com.nimblix.SchoolPEPProject.Repository.TeacherRepository;
import com.nimblix.SchoolPEPProject.Repository.UserRepository;
import com.nimblix.SchoolPEPProject.Request.ClassroomRequest;
import com.nimblix.SchoolPEPProject.Request.TeacherEditProfileRequest;
import com.nimblix.SchoolPEPProject.Request.TeacherRegistrationRequest;
import com.nimblix.SchoolPEPProject.Response.TeacherDetailsResponse;
import com.nimblix.SchoolPEPProject.Response.TeacherProfileResponse;
import com.nimblix.SchoolPEPProject.Service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    // ================= REGISTER TEACHER =================

    @Override
    public Map<String, String> registerTeacher(TeacherRegistrationRequest request) {

        Map<String, String> response = new HashMap<>();

        if (request.getFirstName() == null || request.getFirstName().isBlank()
                || request.getEmailId() == null || request.getEmailId().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            response.put(SchoolConstants.MESSAGE,
                    "Missing required fields (firstName, email, password)");
            return response;
        }

        if (teacherRepository.existsByEmailId(request.getEmailId())) {
            response.put(SchoolConstants.MESSAGE, "Teacher already exists with this email");
            return response;
        }

        if (request.getSchoolId() == null) {
            response.put(SchoolConstants.MESSAGE, "School ID is mandatory");
            return response;
        }

        Role teacherRole = roleRepository.findByRoleName(SchoolConstants.TEACHER_ROLE);

        Teacher teacher = new Teacher();
        teacher.setPrefix(request.getPrefix());
        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setEmailId(request.getEmailId());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));
        teacher.setSchoolId(request.getSchoolId());
        teacher.setRole(teacherRole);
        teacher.setDesignation(SchoolConstants.TEACHER_ROLE);
        teacher.setStatus(SchoolConstants.STATUS_ACTIVE);
        teacher.setIsLogin(false);

        teacherRepository.save(teacher);

        response.put(SchoolConstants.MESSAGE, "Teacher Registered Successfully!");
        return response;
    }


    // ================= CREATE CLASSROOM =================

    @Override
    public ResponseEntity<Map<String, String>> createClassroom(ClassroomRequest request) {

        Map<String, String> response = new HashMap<>();

        List<Classroom> existing = classroomRepository
                .findByClassroomNameAndSchoolId(request.getClassroomName(), request.getSchoolId());

        if (!existing.isEmpty()) {
            response.put("status", "FAIL");
            response.put("message", "Classroom already exists for this school");
            return ResponseEntity.status(409).body(response);
        }

        Classroom classroom = new Classroom();
        classroom.setClassroomName(request.getClassroomName());
        classroom.setSchoolId(request.getSchoolId());
        classroom.setTeacherId(request.getTeacherId());
        classroom.setSubject(request.getSubject());

        classroomRepository.save(classroom);

        response.put("status", "SUCCESS");
        response.put("message", "Classroom created successfully");
        return ResponseEntity.ok(response);
    }

    // ================= GET TEACHER DETAILS =================

    @Override
    public TeacherDetailsResponse getTeacherDetails(Long teacherId) {

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException("Teacher ID must be a positive number");
        }

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() ->
                        new UserNotFoundException("Teacher not found with id: " + teacherId));

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

    // ================= UPDATE TEACHER DETAILS =================

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

        teacher.setSchoolId(request.getSchoolId());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));

        teacherRepository.save(teacher);

        response.put(SchoolConstants.STATUS, SchoolConstants.STATUS_SUCCESS);
        response.put(SchoolConstants.MESSAGE, "Teacher details updated successfully");
        return response;
    }

    // ================= DELETE TEACHER DETAILS =================

    @Override
    public Map<String, String> deleteTeacherDetails(Long teacherId, Long schoolId) {

        Teacher teacher =
                teacherRepository.findByTeacherIdAndSchoolId(teacherId, schoolId);

        if (teacher == null) {
            throw new UserNotFoundException(
                    "Teacher not found or does not belong to school"
            );
        }

        teacherRepository.delete(teacher);

        return Map.of(
                SchoolConstants.STATUS, SchoolConstants.STATUS_SUCCESS,
                SchoolConstants.MESSAGE, "Teacher details deleted successfully"
        );
    }

    // ================= GET TEACHER PROFILE =================

    @Override
    public TeacherProfileResponse getTeacherProfile(Long teacherId, Long schoolId) {

        if (teacherId == null || teacherId <= 0 || schoolId == null || schoolId <= 0) {
            throw new IllegalArgumentException("Invalid teacherId or schoolId");
        }

        Teacher teacher =
                teacherRepository.findByTeacherIdAndSchoolId(teacherId, schoolId);

        if (teacher == null) {
            throw new UserNotFoundException(
                    "Teacher not found or does not belong to school"
            );
        }

        if (!SchoolConstants.TEACHER_ROLE.equalsIgnoreCase(
                teacher.getRole().getRoleName())) {
            throw new IllegalArgumentException("User is not a teacher");
        }

        return TeacherProfileResponse.builder()
                .id(teacher.getId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .emailId(teacher.getEmailId())
                .mobile(teacher.getMobile())
                .address(teacher.getAddress())
                .gender(teacher.getGender())
                .designation(teacher.getDesignation())
                .prefix(teacher.getPrefix())
                .profilePicture(teacher.getProfilePicture())
                .subjects(teacher.getSubjects())
                .build();
    }


    // ================= EDIT TEACHER PROFILE =================

    @Override
    public Map<String, String> editTeacherProfile(
            Long teacherId,
            Long schoolId,
            TeacherEditProfileRequest request) {

        if (teacherId == null || teacherId <= 0 ||
                schoolId == null || schoolId <= 0) {
            throw new IllegalArgumentException("Invalid teacherId or schoolId");
        }

        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        if (request.getFirstName() == null &&
                request.getLastName() == null &&
                request.getMobile() == null &&
                request.getAddress() == null &&
                request.getGender() == null &&
                request.getDesignation() == null &&
                request.getPrefix() == null &&
                request.getProfilePicture() == null) {

            throw new IllegalArgumentException("No fields provided to update");
        }

        Teacher teacher = teacherRepository.findByTeacherIdAndSchoolId(teacherId, schoolId);

        if (teacher == null) {
            throw new UserNotFoundException("Teacher not found or does not belong to school");
        }

        // Update fields
        if (request.getFirstName() != null) teacher.setFirstName(request.getFirstName());
        if (request.getLastName() != null) teacher.setLastName(request.getLastName());
        if (request.getMobile() != null) teacher.setMobile(request.getMobile());
        if (request.getAddress() != null) teacher.setAddress(request.getAddress());
        if (request.getGender() != null) teacher.setGender(request.getGender());
        if (request.getDesignation() != null) teacher.setDesignation(request.getDesignation());
        if (request.getPrefix() != null) teacher.setPrefix(request.getPrefix());
        if (request.getProfilePicture() != null) teacher.setProfilePicture(request.getProfilePicture());



        teacherRepository.save(teacher);

        return Map.of(
                SchoolConstants.STATUS, SchoolConstants.STATUS_SUCCESS,
                SchoolConstants.MESSAGE, "Teacher profile updated successfully"
        );
    }







    // ================= DELETE TEACHER PROFILE =================
    @Override
    public Map<String, String> deleteTeacherProfile(Long teacherId, Long schoolId) {

        if (teacherId == null || teacherId <= 0 ||
                schoolId == null || schoolId <= 0) {
            throw new IllegalArgumentException("Invalid teacherId or schoolId");
        }

        Teacher teacher =
                teacherRepository.findByTeacherIdAndSchoolId(teacherId, schoolId);

        if (teacher == null) {
            throw new UserNotFoundException(
                    "Teacher not found or does not belong to school"
            );
        }

        if (SchoolConstants.STATUS_INACTIVE
                .equalsIgnoreCase(teacher.getStatus())) {

            throw new IllegalArgumentException("Teacher already inactive");
        }


        teacher.setStatus(SchoolConstants.STATUS_INACTIVE);
        teacher.setIsLogin(false);


        teacherRepository.save(teacher);

        return Map.of("message", "Teacher soft deleted successfully");
    }



}
