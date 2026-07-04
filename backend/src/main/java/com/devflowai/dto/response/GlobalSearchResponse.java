package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GlobalSearchResponse {
    private List<ProjectResponse> projects;
    private List<TaskResponse> tasks;
    private List<NoteResponse> notes;
    private List<DocResponse> docs;
}
