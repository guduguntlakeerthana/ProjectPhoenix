package com.devflowai.repository;

import com.devflowai.entity.Note;
import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);
    List<Note> findByProjectAndUser(Project project, User user);
    Optional<Note> findByIdAndUser(Long id, User user);
}
