package com.devflowai.repository;

import com.devflowai.entity.Project;
import com.devflowai.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);
    List<ProjectMember> findByEmail(String email);
    Optional<ProjectMember> findByProjectAndEmail(Project project, String email);
}
