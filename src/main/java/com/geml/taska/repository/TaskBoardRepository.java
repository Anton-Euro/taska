package com.geml.taska.repository;

import com.geml.taska.models.TaskBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskBoardRepository extends JpaRepository<TaskBoard, Long> {
    @Query("SELECT t FROM TaskBoard t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<TaskBoard> searchByTitle(@Param("title") String title);

    List<TaskBoard> findByUserId(Long userId);
}