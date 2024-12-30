package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item);

    List<ItemDto> getAllItemByOwnerId(Long ownerId);

    ItemDto getItemById(Long itemId);

    ItemDto updateItem(ItemDto item);

    void deleteItemByItemId(Long itemId);

    List<ItemDto> searchItemsByText(String text);
}