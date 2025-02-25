package com.geml.taska.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTaskDto {

    private Long id;
    private String title;
    private String description;
    private Long userId;
}