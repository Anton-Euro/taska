package com.geml.taska.repository;

import com.geml.taska.models.Notebook;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    @Query("SELECT t FROM Notebook t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Notebook> searchByTitle(@Param("title") String title);

    Set<Notebook> findByTagsId(Long tagId);

    List<Notebook> findByTaskId(Long taskId);

    List<Notebook> findByUserId(Long userId);
}