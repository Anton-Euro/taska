package com.geml.taska.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayNotebookFullDto {

    private Long id;
    private String title;
    private String content;
    private DisplayUserDto user;
    private DisplayTaskItemDto taskItem;
    private Set<DisplayTagDto> tags;
}
