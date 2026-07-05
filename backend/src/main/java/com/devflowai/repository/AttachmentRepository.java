package com.devflowai.repository;

import com.devflowai.entity.Attachment;
import com.devflowai.entity.Project;
import com.devflowai.entity.Task;
import com.devflowai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByUser(User user);
    List<Attachment> findByProject(Project project);
    List<Attachment> findByTask(Task task);
}
