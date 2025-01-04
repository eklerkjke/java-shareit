package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Headers;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(name = Headers.HEADER_USER_ID) Long ownerId, @Valid @RequestBody ItemDto item) {
        item.setOwnerId(ownerId);
        return itemService.createItem(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto item,
                              @RequestHeader(name = Headers.HEADER_USER_ID) Long ownerId) {
        item.setId(itemId);
        item.setOwnerId(ownerId);

        return itemService.updateItem(item);
    }

    @GetMapping
    public List<ItemDto> getAllItemByOwner(@RequestHeader(name = Headers.HEADER_USER_ID) Long ownerId) {
        return itemService.getAllItemByOwnerId(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }
}