package com.nimblix.SchoolPEPProject.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TeacherDetailsResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String mobile;
    private String prefix;
    private String designation;
    private String gender;
    private String status;

    private List<Subjectresponse> subjects;

    @Getter
    @Setter
    @Builder
    public static class Subjectresponse{
        private Long subjectId;
        private String subjectName;
        private String subjectCode;
        private Long classRoomId;

    }
}
