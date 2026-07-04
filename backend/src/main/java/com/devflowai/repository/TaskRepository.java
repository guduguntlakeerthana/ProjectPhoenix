package com.devflowai.repository;

import com.devflowai.entity.Project;
import com.devflowai.entity.Task;
import com.devflowai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByProjectAndUser(Project project, User user);
    Optional<Task> findByIdAndUser(Long id, User user);
}
