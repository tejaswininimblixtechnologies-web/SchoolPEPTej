package com.nimblix.SchoolPEPProject.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherEditProfileRequest {

    private String firstName;
    private String lastName;
    private String mobile;
    private String address;
    private String gender;
    private String designation;
    private String prefix;
    private String profilePicture;
}

