package com.geml.taska.service;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.mapper.TagMapper;
import com.geml.taska.models.Tag;
import com.geml.taska.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Сервис для управления тегами.
 */
@Service
public class TagService {

  private final TagRepository tagRepository;
  private final TagMapper tagMapper;

  /**
   * Конструктор.
   *
   * @param tagRepository репозиторий тегов
   * @param tagMapper маппер для Tag
   */
  public TagService(final TagRepository tagRepository,
                    final TagMapper tagMapper) {
    this.tagRepository = tagRepository;
    this.tagMapper = tagMapper;
  }

  /**
   * Получить все теги.
   *
   * @return список TagDto
   */
  public List<DisplayTagDto> getAllTags() {
    return tagRepository.findAll().stream()
        .map(tagMapper::toDisplayTagDto)
        .collect(Collectors.toList());
  }

  /**
   * Получить тег по идентификатору.
   *
   * @param id идентификатор тега
   * @return TagDto
   * @throws ResponseStatusException если тег не найден (404)
   */
  public DisplayTagDto getTagById(final Long id) {
    Tag tag = tagRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
    return tagMapper.toDisplayTagDto(tag);
  }

  /**
   * Создать тег.
   *
   * @param dto объект TagDto
   * @return созданный TagDto
   */
  public DisplayTagDto createTag(final CreateTagDto dto) {
    Tag tag = tagMapper.fromCreateTagDto(dto);
    Tag saved = tagRepository.save(tag);
    return tagMapper.toDisplayTagDto(saved);
  }

  /**
   * Обновить тег.
   *
   * @param id идентификатор тега
   * @param dto обновленные данные
   * @return обновленный TagDto
   * @throws ResponseStatusException если тег не найден (404)
   */
  public DisplayTagDto updateTag(final Long id, final CreateTagDto dto) {
    Tag tag = tagRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    tag.setName(dto.getName());
    Tag saved = tagRepository.save(tag);
    return tagMapper.toDisplayTagDto(saved);
  }

  /**
   * Удалить тег.
   *
   * @param id идентификатор тега
   * @throws ResponseStatusException если тег не найден (404)
   */
  public void deleteTag(final Long id) {
    if (!tagRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    tagRepository.deleteById(id);
  }
}