package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.models.Tag;
import org.springframework.stereotype.Component;


@Component
public class TagMapper {


    public DisplayTagDto toDisplayTagDto(final Tag tag) {
        return new DisplayTagDto(tag.getId(), tag.getName());
    }


    public Tag fromCreateTagDto(final CreateTagDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        return tag;
    }
}
