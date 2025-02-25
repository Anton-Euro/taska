package com.geml.taska.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayNotebookDto {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Long taskItemId;
    private Set<Long> tagIds;
}