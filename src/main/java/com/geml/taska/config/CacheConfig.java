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
    private static final String ALL_NOTEBOOKS_CACHE_KEY = "allNotebooks";
    private static final String ALL_NOTEBOOKS_FULL_CACHE_KEY = "allNotebooksFull";

    private final LruCache<String, List<DisplayNotebookDto>> allNotebooksCache = 
        new LruCache<>(MAX_NOTEBOOKS_CACHE_SIZE);
    private final LruCache<String, List<DisplayNotebookFullDto>> allNotebooksFullCache = 
        new LruCache<>(MAX_NOTEBOOKS_CACHE_SIZE);

    public List<DisplayNotebookDto> getAllNotebooks() {
        log.debug("Запрос к кэшу всех notebooks");
        return allNotebooksCache.get(ALL_NOTEBOOKS_CACHE_KEY);
    }

    public void putAllNotebooks(List<DisplayNotebookDto> notebooks) {
        log.debug("Добавление всех notebooks в кэш");
        allNotebooksCache.put(ALL_NOTEBOOKS_CACHE_KEY, notebooks);
    }

    public void removeAllNotebooks() {
        log.debug("Удаление всех notebooks из кэша");
        allNotebooksCache.remove(ALL_NOTEBOOKS_CACHE_KEY);
    }

    public List<DisplayNotebookFullDto> getAllNotebooksFull() {
        log.debug("Запрос к кэшу всех notebooks full");
        return allNotebooksFullCache.get(ALL_NOTEBOOKS_FULL_CACHE_KEY);
    }

    public void putAllNotebooksFull(List<DisplayNotebookFullDto> notebooksFull) {
        log.debug("Добавление всех notebooks full в кэш");
        allNotebooksFullCache.put(ALL_NOTEBOOKS_FULL_CACHE_KEY, notebooksFull);
    }

    public void removeAllNotebooksFull() {
        log.debug("Удаление всех notebooks full из кэша");
        allNotebooksFullCache.remove(ALL_NOTEBOOKS_FULL_CACHE_KEY);
    }
}
