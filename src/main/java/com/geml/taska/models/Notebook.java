package com.geml.taska.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a notebook.
 */
@Entity
@Table(name = "notebooks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notebook {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String content;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "task_item_id")
  private TaskItem taskItem;

  /**
   * ManyToMany связь с тегами.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "notebook_tags",
      joinColumns = @JoinColumn(name = "notebook_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags;
}

