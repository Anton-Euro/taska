package com.geml.taska.repository;

import com.geml.taska.models.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT t FROM Board t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Board> searchByTitle(@Param("title") String title);

    List<Board> findByUserId(Long userId);
}