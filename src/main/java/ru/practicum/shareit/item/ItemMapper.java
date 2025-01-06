package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public class ItemMapper {
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