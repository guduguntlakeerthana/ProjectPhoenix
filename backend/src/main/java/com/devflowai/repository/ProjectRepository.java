package com.devflowai.repository;

import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUser(User user);

    Optional<Project> findByIdAndUser(Long id, User user);

    @Query("SELECT p FROM Project p WHERE p.user = :user " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.techStack) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> findByUserAndFilters(
            @Param("user") User user,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );
}