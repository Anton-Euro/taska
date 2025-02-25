package com.geml.taska.repository;

import com.geml.taska.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Task entity.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}