package com.nimblix.SchoolPEPProject.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRegistrationRequest {

    private String prefix;
    private String firstName;
    private String lastName;

    private String emailId;
    private String password;
    private Long schoolId;
}
