package com.geml.taska.repository;

import com.geml.taska.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TagRepository extends JpaRepository<Tag, Long> {
}