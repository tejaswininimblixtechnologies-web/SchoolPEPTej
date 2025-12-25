package com.nimblix.SchoolPEPProject.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StudentResponse {

    private Long studentId;
    private String firstName;
    private String lastName;
    private String dob;
    private String mobileNo;
    private String emailId;
    private String rollNo;
    private String admissionNo;
    private String registrationNo;
    private String className;
    private String section;
    private String profileImage;

    private List<SubjectPerformance> subjects;

    @Getter
    @Setter
    @Builder
    public static class SubjectPerformance {
        private String subjectName;
        private Integer totalMarks;
        private Integer marksObtained;
        private String grade;
    }
}
