package com.geml.taska.config;

import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.dto.DisplayNotebookFullDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CacheConfig {
    private static final int MAX_NOTEBOOKS_CACHE_SIZE = 100;

    private final LruCache<String, List<DisplayNotebookDto>> allNotebooksCache = 
        new LruCache<>(MAX_NOTEBOOKS_CACHE_SIZE);
    private final LruCache<String, List<DisplayNotebookFullDto>> allNotebooksFullCache = 
        new LruCache<>(MAX_NOTEBOOKS_CACHE_SIZE);

    public List<DisplayNotebookDto> getAllNotebooks() {
        log.debug("Запрос к кэшу всех notebooks");
        return allNotebooksCache.get("allNotebooks");
    }

    public void putAllNotebooks(List<DisplayNotebookDto> notebooks) {
        log.debug("Добавление всех notebooks в кэш");
        allNotebooksCache.put("allNotebooks", notebooks);
    }

    public void removeAllNotebooks() {
        log.debug("Удаление всех notebooks из кэша");
        allNotebooksCache.remove("allNotebooks");
    }

    public List<DisplayNotebookFullDto> getAllNotebooksFull() {
        log.debug("Запрос к кэшу всех notebooks full");
        return allNotebooksFullCache.get("allNotebooksFull");
    }

    public void putAllNotebooksFull(List<DisplayNotebookFullDto> notebooksFull) {
        log.debug("Добавление всех notebooks full в кэш");
        allNotebooksFullCache.put("allNotebooksFull", notebooksFull);
    }

    public void removeAllNotebooksFull() {
        log.debug("Удаление всех notebooks full из кэша");
        allNotebooksFullCache.remove("allNotebooksFull");
    }
}
