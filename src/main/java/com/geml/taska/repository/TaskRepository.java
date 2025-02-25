package com.geml.taska.repository;

import com.geml.taska.models.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for Task entity.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
  @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
  List<Task> searchByTitle(@Param("title") String title);
}