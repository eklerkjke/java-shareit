package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public class ItemMapper {
    // Преобразует объект Item в объект ItemDto.
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
    }

    // Обновляет объект Item на основе данных из ItemDto.
    // Если поля в ItemDto отсутствуют, используются существующие значения из Item.
    public static Item toItemOnUpdate(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId())
                .name(Optional.ofNullable(itemDto.getName()).orElse(item.getName()))
                .description(Optional.ofNullable(itemDto.getDescription()).orElse(item.getDescription()))
                .available(Optional.ofNullable(itemDto.getAvailable()).orElse(item.getAvailable()))
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
    }

    // Преобразует объект ItemDto в объект Item.
    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(itemDto.getOwnerId())
                .requestId(itemDto.getRequestId())
                .build();
    }
}