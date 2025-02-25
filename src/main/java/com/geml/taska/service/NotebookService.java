package com.geml.taska.service;

import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.mapper.NotebookMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import com.geml.taska.models.TaskItem;
import com.geml.taska.models.User;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TagRepository;
import com.geml.taska.repository.TaskItemRepository;
import com.geml.taska.repository.TaskRepository;
import com.geml.taska.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Сервис для управления блокнотами.
 */
@Service
public class NotebookService {

  private final NotebookRepository notebookRepository;
  private final NotebookMapper notebookMapper;
  private final UserRepository userRepository;
  private final TaskItemRepository taskItemRepository;
  private final TagRepository tagRepository;

  /**
   * Конструктор.
   *
   * @param notebookRepository репозиторий блокнотов
   * @param notebookMapper маппер для Notebook
   * @param userRepository репозиторий пользователей
   * @param taskItemRepository репозиторий пунктов задач
   * @param taskRepository репозиторий задач
   * @param tagRepository репозиторий тегов
   */
  public NotebookService(final NotebookRepository notebookRepository,
                         final NotebookMapper notebookMapper,
                         final UserRepository userRepository,
                         final TaskItemRepository taskItemRepository,
                         final TaskRepository taskRepository,
                         final TagRepository tagRepository) {
    this.notebookRepository = notebookRepository;
    this.notebookMapper = notebookMapper;
    this.userRepository = userRepository;
    this.taskItemRepository = taskItemRepository;
    this.tagRepository = tagRepository;
  }

  /**
   * Получить все блокноты.
   *
   * @return список NotebookDto
   */
  public List<DisplayNotebookDto> getAllNotebooks() {
    return notebookRepository.findAll().stream()
        .map(notebookMapper::toDisplayNotebookDto)
        .collect(Collectors.toList());
  }

  /**
   * Получить блокнот по идентификатору.
   *
   * @param id идентификатор блокнота
   * @return NotebookDto
   */
  public DisplayNotebookDto getNotebookById(final Long id) {
    Notebook nb = notebookRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notebook not found"));
    return notebookMapper.toDisplayNotebookDto(nb);
  }

  /**
   * Создать блокнот.
   *
   * @param dto объект NotebookDto
   * @return созданный NotebookDto
   */
  public DisplayNotebookDto createNotebook(final CreateNotebookDto dto) {
    Notebook nb = notebookMapper.fromCreateNotebookDto(dto);
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    nb.setUser(user);
    if (dto.getTaskItemId() != null) {
      TaskItem taskItem = taskItemRepository.findById(dto.getTaskItemId())
          .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task item not found")
          );
      nb.setTaskItem(taskItem);
    }
    if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
      Set<Tag> tags = dto.getTagIds().stream()
          .map(tagId -> tagRepository.findById(tagId)
              .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"))
              )
          .collect(Collectors.toSet());
      nb.setTags(tags);
    }
    Notebook saved = notebookRepository.save(nb);
    return notebookMapper.toDisplayNotebookDto(saved);
  }

  /**
   * Обновить блокнот.
   *
   * @param id идентификатор блокнота
   * @param dto обновленные данные
   * @return обновленный NotebookDto
   */
  public DisplayNotebookDto updateNotebook(final Long id, final CreateNotebookDto dto) {
    Notebook nb = notebookRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    nb.setTitle(dto.getTitle());
    nb.setContent(dto.getContent());
    if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
      Set<Tag> tags = dto.getTagIds().stream()
          .map(tagId -> tagRepository.findById(tagId)
              .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND))
              )
          .collect(Collectors.toSet());
      nb.setTags(tags);
    }
    Notebook saved = notebookRepository.save(nb);
    return notebookMapper.toDisplayNotebookDto(saved);
  }

  /**
   * Удалить блокнот.
   *
   * @param id идентификатор блокнота
   */
  public void deleteNotebook(final Long id) {
    if (!notebookRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notebook not found");
    }
    notebookRepository.deleteById(id);
  }
}