package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item createItem(Item item) {
        item.setId(id);
        items.put(id++, item);
        log.info("Создан новый предмет: {}", item);
        return item;
    }

    @Override
    public List<Item> getAllItemByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.getOrDefault(itemId, null));
    }

    @Override
    public Item updateItem(Item item) {
        Long id = item.getId();
        Item updateItem = items.getOrDefault(id, null);
        if (updateItem == null) {
            throw new NotFoundException("Не удалось найти предмет с id: " + id);
        }

        updateItem.setName(item.getName());
        updateItem.setDescription(item.getDescription());
        updateItem.setAvailable(item.getAvailable());

        log.info("Предмет с ID: {} успешно обновлен", id);
        return updateItem;
    }

    @Override
    public void deleteItemByItemId(Long itemId) {
        if (items.containsKey(itemId)) {
            items.remove(itemId);
        } else {
            throw new NotFoundException("Не удалось найти предмет с id: " + itemId);
        }
        log.info("Предмет с ID: {} успешно удален", itemId);
    }

    /* Ищет предметы по текстовому запросу. Поиск выполняется без учета регистра
       и соответствует тексту в названиях и описаниях предметов. */
    @Override
    public List<Item> searchItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .toList();
    }
}