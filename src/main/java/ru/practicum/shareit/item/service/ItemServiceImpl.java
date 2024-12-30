package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto createItem(ItemDto item) {
        userService.getUserById(item.getOwnerId());
        log.info("Создание нового предмета: {}", item);
        return ItemMapper.toItemDto(itemRepository.createItem(ItemMapper.toItem(item)));
    }

    @Override
    public List<ItemDto> getAllItemByOwnerId(Long ownerId) {
        log.info("Получение всех предметов по ID владельца: {}", ownerId);
        userService.getUserById(ownerId);
        return itemRepository.getAllItemByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Получение предмета по ID: {}", itemId);
        return ItemMapper.toItemDto(findById(itemId));
    }

    @Override
    public ItemDto updateItem(ItemDto item) {
        log.info("Обновление предмета по ID: {}", item.getId());
        userService.getUserById(item.getOwnerId());
        return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItemOnUpdate(item, findById(item.getId()))));
    }

    @Override
    public void deleteItemByItemId(Long itemId) {
        log.info("Удаление предмета по ID: {}", itemId);
        itemRepository.deleteItemByItemId(itemId);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        log.info("Поиск предметов по тексту: {}", text);
        return itemRepository.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private Item findById(Long itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден предмет с id: " + itemId));
    }
}