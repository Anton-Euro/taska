package com.geml.taska.repository;

import com.geml.taska.models.Notebook;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    @Query("SELECT t FROM Notebook t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Notebook> searchByTitle(@Param("title") String title);
}