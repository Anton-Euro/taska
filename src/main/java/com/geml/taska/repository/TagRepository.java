package com.geml.taska.repository;

import com.geml.taska.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Tag entity.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
}