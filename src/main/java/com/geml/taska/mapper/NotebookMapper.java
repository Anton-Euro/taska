package com.geml.taska.mapper;

import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class NotebookMapper {

    public DisplayNotebookDto toDisplayNotebookDto(final Notebook nb) {
        Set<Long> tagIds = new HashSet<>();
        if (nb.getTags() != null) {
            tagIds = nb.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        }
        return new DisplayNotebookDto(
                nb.getId(),
                nb.getTitle(),
                nb.getContent(),
                nb.getUser().getId(),
                nb.getTaskItem() != null ? nb.getTaskItem().getId() : null,
                tagIds);
    }


    public Notebook fromCreateNotebookDto(final CreateNotebookDto dto) {
        Notebook nb = new Notebook();
        nb.setTitle(dto.getTitle());
        nb.setContent(dto.getContent());
        return nb;
    }
}
