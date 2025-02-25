package com.geml.taska.repository;

import com.geml.taska.models.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Notebook entity.
 */
public interface NotebookRepository extends JpaRepository<Notebook, Long> {
}