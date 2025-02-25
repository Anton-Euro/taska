package com.geml.taska.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

  private String username;
  private String email;
  private String password;
}