package com.devflowai.repository;

import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUser(User user);

    Optional<Project> findByIdAndUser(Long id, User user);
}