package com.nimblix.SchoolPEPProject.Response;

import com.nimblix.SchoolPEPProject.Model.Subjects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TeacherProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String mobile;
    private String address;
    private String gender;
    private String designation;
    private String prefix;
    private String profilePicture;
    private List<Subjects> subjects;
}
