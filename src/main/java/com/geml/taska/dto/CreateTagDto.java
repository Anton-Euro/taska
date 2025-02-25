package com.geml.taska.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Tag.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagDto {

  private String name;
}