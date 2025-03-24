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

    @Query(value = """
        SELECT
            n.id AS notebook_id,
            n.title AS notebook_title,
            n.content AS notebook_content,
            t.id AS tag_id,
            t.name AS tag_name,
            task.id AS task_id,
            task.title AS task_title
        FROM
            notebooks n
        LEFT JOIN
            notebook_tags nt ON n.id = nt.notebook_id
        LEFT JOIN
            tags t ON nt.tag_id = t.id
        LEFT JOIN
            tasks task ON n.task_id = task.id
        WHERE (:tagName IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :tagName, '%')))
        """, nativeQuery = true)
    List<Object[]> findAllNotebooksFullWithTagFilter(@Param("tagName") String tagName);
}
