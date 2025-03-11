package com.geml.taska.mapper;

import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.dto.DisplayNotebookFullDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class NotebookMapper {
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;
    private final TagMapper tagMapper;

    public NotebookMapper(
        UserMapper userMapper, 
        TaskMapper taskMapper, 
        TagMapper tagMapper
    ) {
        this.userMapper = userMapper;
        this.taskMapper = taskMapper;
        this.tagMapper = tagMapper;
    }

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
                nb.getTask() != null ? nb.getTask().getId() : null,
                tagIds);
    }

    public DisplayNotebookFullDto toDisplayNotebookFullDto(final Notebook nb) {
        Set<DisplayTagDto> tags = new HashSet<>();
        if (nb.getTags() != null) {
            tags = nb.getTags().stream()
                .map(tagMapper::toDisplayTagDto).collect(Collectors.toSet());
        }
        return new DisplayNotebookFullDto(
                nb.getId(),
                nb.getTitle(),
                nb.getContent(),
                userMapper.toDisplayUserDto(nb.getUser()),
                nb.getTask() != null ? taskMapper.toDisplayTaskDto(
                    nb.getTask()
                ) : null,
                tags);
    }


    public Notebook fromCreateNotebookDto(final CreateNotebookDto dto) {
        Notebook nb = new Notebook();
        nb.setTitle(dto.getTitle());
        nb.setContent(dto.getContent());
        return nb;
    }
}
