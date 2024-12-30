package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item createItem(Item item);

    List<Item> getAllItemByOwnerId(Long ownerId);

    Optional<Item> getItemById(Long itemId);

    Item updateItem(Item item);

    void deleteItemByItemId(Long itemId);

    List<Item> searchItemsByText(String text);
}