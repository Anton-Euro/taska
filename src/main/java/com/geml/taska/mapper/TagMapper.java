package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.models.Tag;
import org.springframework.stereotype.Component;

/**
 * Mapper для сущности Tag.
 */
@Component
public class TagMapper {

  /**
   * Преобразование Tag в TagDTO.
   *
   * @param tag сущность тега
   * @return TagDTO
   */
  public DisplayTagDto toDisplayTagDto(final Tag tag) {
    return new DisplayTagDto(tag.getId(), tag.getName());
  }

  /**
   * Преобразование TagDTO в Tag.
   *
   * @param dto объект TagDTO
   * @return сущность Tag
   */
  public Tag fromCreateTagDto(final CreateTagDto dto) {
    Tag tag = new Tag();
    tag.setName(dto.getName());
    return tag;
  }
}

