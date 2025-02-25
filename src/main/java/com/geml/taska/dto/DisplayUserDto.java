package com.geml.taska.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayUserDto {

    private Long id;
    private String username;
    private String email;
}