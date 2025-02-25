package com.geml.taska.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a task item.
 */
@Entity
@Table(name = "task_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private Boolean completed;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @OneToMany(mappedBy = "taskItem", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notebook> notebooks;
}

