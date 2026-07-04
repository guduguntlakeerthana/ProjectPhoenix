package com.devflowai.repository;

import com.devflowai.entity.Doc;
import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocRepository extends JpaRepository<Doc, Long> {
    List<Doc> findByUser(User user);
    List<Doc> findByProjectAndUser(Project project, User user);
    Optional<Doc> findByIdAndUser(Long id, User user);
}
