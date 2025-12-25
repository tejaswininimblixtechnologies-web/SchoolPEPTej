package com.nimblix.SchoolPEPProject.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignmentShareRequest {

    private Long classId;
    private String section;
    private List<String> shareChannels;
}
