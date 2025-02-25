package com.geml.taska.repository;

import com.geml.taska.models.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
}