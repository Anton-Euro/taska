package com.geml.taska.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for TaskItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTaskItemDto {

  private Long id;
  private String title;
  private Boolean completed;
  private Long taskId;
}