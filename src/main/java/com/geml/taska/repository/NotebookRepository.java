package com.geml.taska.repository;

import com.geml.taska.models.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotebookRepository extends JpaRepository<Notebook, Long> {
}