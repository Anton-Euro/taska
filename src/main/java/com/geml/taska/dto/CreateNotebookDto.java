package com.geml.taska.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotebookDto {

    private String title;
    private String content;
    private Long userId;
    private Long taskId;
    private Set<Long> tagIds;
}