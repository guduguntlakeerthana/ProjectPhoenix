package com.devflowai.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectMemberRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(COLLABORATOR|VIEWER)$", message = "Role must be COLLABORATOR or VIEWER")
    private String role;

    @NotNull(message = "Project ID is required")
    private Long projectId;
}
